package ec.veronica.job.processor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import ec.veronica.common.ReceiptDetails;
import ec.veronica.common.SriUtils;
import ec.veronica.common.StringUtils;
import ec.veronica.common.types.SriStatusType;
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

import static ec.veronica.common.XmlUtils.evalXPath;
import static ec.veronica.common.types.SriStatusType.STATUS_APPLIED;
import static ec.veronica.common.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static ec.veronica.common.types.SriStatusType.STATUS_NOT_APPLIED;
import static ec.veronica.common.types.SriStatusType.STATUS_PENDING;
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
            ReceiptDetails receiptDetails = SriUtils.getDetails(new String(payload), "");
            log.info("Processing receipt for supplier {} and receipt: {}-{}-{}-{}",
                    receiptDetails.getSupplierNumber(),
                    receiptDetails.getDocumentType().getShortName(),
                    receiptDetails.getEstablishment(),
                    receiptDetails.getEmissionPoint(),
                    receiptDetails.getDocumentNumber());
            String responseBody = veronicaHttpClient.postAndApply(payload);
            SriStatusType sriStatusType = getSriStatus(responseBody);
            log(responseBody, sriStatusType, receiptDetails);
            String accessKey = getAccessKey(responseBody, sriStatusType);
            byte[] pdf = sriStatusType == STATUS_APPLIED ? veronicaHttpClient.getFile(accessKey, "pdf") : null;
            byte[] xml = sriStatusType == STATUS_APPLIED || sriStatusType == STATUS_NOT_APPLIED ? veronicaHttpClient.getFile(accessKey, "xml") : payload;
            processorFactory.get(sriStatusType).process(exchange, pdf, xml);
            exchange.getIn().setHeader("folderName", getCustomerNumber(receiptDetails.getDocument(), receiptDetails));
            exchange.getIn().setHeader("fileName", getFileName(receiptDetails));
            log.info("Process completed");
        } catch (Exception ex) {
            log.error("An error has occurred trying to apply the receipt: {}", ex.getMessage());
            processorFactory.get(STATUS_INTERNAL_ERROR).process(exchange, null, null);
        }
    }

    /**
     * @param receiptDetails
     * @return
     */
    private String getFileName(ReceiptDetails receiptDetails) {
        return format("%s-%s-%s-%s",
                receiptDetails.getDocumentType().getShortName(),
                receiptDetails.getEstablishment(),
                receiptDetails.getEmissionPoint(),
                receiptDetails.getDocumentNumber()
        );
    }

    /**
     * @param document
     * @param sriEntity
     * @return
     * @throws XPathExpressionException
     */
    private String getCustomerNumber(Document document, ReceiptDetails sriEntity) throws XPathExpressionException {
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
     * @param receiptDetails
     */
    private void log(String responseBody, SriStatusType status, ReceiptDetails receiptDetails) {
        Log logEntry = Log
                .builder()
                .estab(receiptDetails.getEstablishment())
                .ptoEmision(receiptDetails.getEmissionPoint())
                .receiptNumber(receiptDetails.getDocumentNumber())
                .docType(receiptDetails.getDocumentType().getNombre())
                .response(responseBody)
                .status(status.getValue())
                .supplierNumber(receiptDetails.getSupplierNumber())
                .insertionDate(LocalDateTime.now())
                .build();
        logService.save(logEntry);
    }

}
