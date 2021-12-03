package ec.veronica.job.processor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.rolandopalermo.facturacion.ec.common.SriEntity;
import com.rolandopalermo.facturacion.ec.common.StringUtils;
import com.rolandopalermo.facturacion.ec.common.XmlUtils;
import com.rolandopalermo.facturacion.ec.common.types.SriStatusType;
import ec.veronica.job.domain.Log;
import ec.veronica.job.factory.ExtractorFactory;
import ec.veronica.job.factory.ProcessorFactory;
import ec.veronica.job.http.VeronicaHttpClient;
import ec.veronica.job.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.rolandopalermo.facturacion.ec.common.SriUtils.getReceiptDetails;
import static com.rolandopalermo.facturacion.ec.common.XmlUtils.evalXPath;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_APPLIED;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_NOT_APPLIED;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_PENDING;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileProcessor implements Processor {

    private final LogService logService;
    private final ExtractorFactory extractorFactory;
    private final ProcessorFactory processorFactory;
    private final VeronicaHttpClient veronicaHttpClient;

    @Override
    public void process(Exchange exchange) {
        try {
            byte[] payload = exchange.getIn().getBody(String.class).getBytes(StandardCharsets.UTF_8);
            Document document = XmlUtils.fromStringToDocument(new String(payload));
            SriEntity sriEntity = getReceiptDetails(document, "");
            log.info("Start processing for supplier {} and receipt: {}-{}-{}-{}",
                    sriEntity.getSupplierNumber(),
                    sriEntity.getDocumentType().getShortName(),
                    sriEntity.getEstablishment(),
                    sriEntity.getEmissionPoint(),
                    sriEntity.getDocumentNumber());
            String responseBody = veronicaHttpClient.postAndApply(payload);
            SriStatusType sriStatusType = getSriStatus(responseBody);
            log(responseBody, sriStatusType, sriEntity);
            String accessKey = getAccessKey(responseBody, sriStatusType);
            byte[] pdf = sriStatusType == STATUS_APPLIED ? veronicaHttpClient.getFile(accessKey, "pdf") : null;
            byte[] xml = sriStatusType == STATUS_APPLIED || sriStatusType == STATUS_NOT_APPLIED ? veronicaHttpClient.getFile(accessKey, "xml") : payload;
            processorFactory.get(sriStatusType).process(exchange, pdf, xml);
            exchange.getIn().setHeader("folderName", getCustomerNumber(document, sriEntity));
            exchange.getIn().setHeader("fileName", getFileName(sriEntity));
        } catch (Exception ex) {
            log.error("An error has occurred trying to apply the receipt: {}", ex.getMessage());
            processorFactory.get(STATUS_INTERNAL_ERROR).process(exchange, null, null);
        }
    }

    /**
     * @param sriEntity
     * @return
     */
    private String getFileName(SriEntity sriEntity) {
        return format("%s-%s-%s-%s",
                sriEntity.getDocumentType().getShortName(),
                sriEntity.getEstablishment(),
                sriEntity.getEmissionPoint(),
                sriEntity.getDocumentNumber()
        );
    }

    /**
     * @param document
     * @param sriEntity
     * @return
     * @throws XPathExpressionException
     */
    private String getCustomerNumber(Document document, SriEntity sriEntity) throws XPathExpressionException {
        return evalXPath(document, extractorFactory.get(sriEntity.getDocumentType()).getCustomerNumberXPath()).get();
    }

    /**
     * @param responseBody
     * @param sriStatusType
     * @return
     */
    private String getAccessKey(String responseBody, SriStatusType sriStatusType) {
        return sriStatusType == STATUS_APPLIED ? JsonPath.read(responseBody, "$.result.autorizaciones[0].numeroAutorizacion") :
                sriStatusType == STATUS_NOT_APPLIED ? JsonPath.read(responseBody, "$.result.claveAccesoConsultada") : "";
    }

    /**
     * @param responseBody
     * @return
     */
    private SriStatusType getSriStatus(String responseBody) {
        if (StringUtils.isEmpty(responseBody)) {
            return STATUS_INTERNAL_ERROR;
        }
        Optional<SriStatusType> status;
        try {
            status = SriStatusType.fromValue(JsonPath.read(responseBody, "$.result.autorizaciones[0].estado"));
        } catch (PathNotFoundException ex1) {
            try {
                status = SriStatusType.fromValue(JsonPath.read(responseBody, "$.result.estado"));
            } catch (PathNotFoundException ex2) {
                status = Optional.of(STATUS_INTERNAL_ERROR);
            }
        }
        return status.orElse(STATUS_PENDING);
    }

    /**
     * @param responseBody
     * @param status
     * @param sriEntity
     */
    private void log(String responseBody, SriStatusType status, SriEntity sriEntity) {
        Log logEntry = Log
                .builder()
                .estab(sriEntity.getEstablishment())
                .ptoEmision(sriEntity.getEmissionPoint())
                .receiptNumber(sriEntity.getDocumentNumber())
                .docType(sriEntity.getDocumentType().getNombre())
                .response(responseBody)
                .status(status.getValue())
                .supplierNumber(sriEntity.getSupplierNumber())
                .insertionDate(LocalDateTime.now())
                .build();
        logService.save(logEntry);
    }

}
