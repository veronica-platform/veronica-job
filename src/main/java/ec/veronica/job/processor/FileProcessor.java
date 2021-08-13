package ec.veronica.job.processor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.rolandopalermo.facturacion.ec.common.StringUtils;
import com.rolandopalermo.facturacion.ec.common.XmlUtils;
import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import com.rolandopalermo.facturacion.ec.common.types.DocumentType;
import com.rolandopalermo.facturacion.ec.common.types.SriStatusType;
import ec.veronica.job.factory.ExtractorFactory;
import ec.veronica.job.factory.ProcessorFactory;
import ec.veronica.job.http.VeronicaHttpClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_APPLIED;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_NOT_APPLIED;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_PENDING;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileProcessor implements Processor {

    private final ExtractorFactory extractorFactory;
    private final ProcessorFactory processorFactory;
    private final VeronicaHttpClient veronicaHttpClient;

    @Override
    public void process(Exchange exchange) {
        byte[] payload = exchange.getIn().getBody(String.class).getBytes(StandardCharsets.UTF_8);
        Document document = XmlUtils.fromStringToDocument(new String(payload));
        ReceiptDetails receiptDetails = readDetails(document);
        String responseBody = veronicaHttpClient.sendReceipt(payload);
        log.debug("SRI response for receipt {} : {}", receiptDetails.getDocNumber(), responseBody);
        SriStatusType sriStatusType = getSriStatus(responseBody);
        String accessKey = getAccessKey(responseBody, sriStatusType);
        byte[] pdf = sriStatusType == STATUS_APPLIED ? veronicaHttpClient.getReceiptFile(accessKey, "pdf") : null;
        byte[] xml = sriStatusType == STATUS_APPLIED || sriStatusType == STATUS_NOT_APPLIED ? veronicaHttpClient.getReceiptFile(accessKey, "xml") : payload;
        processorFactory.get(sriStatusType).process(exchange, pdf, xml);
        exchange.getIn().setHeader("folderName", receiptDetails.getCustomerNumber());
        exchange.getIn().setHeader("fileName", format("%s-%s-%s-%s",
                receiptDetails.getDocumentType().getShortName(),
                receiptDetails.getEstablishment(),
                receiptDetails.getEmissionPoint(),
                receiptDetails.getDocNumber()));
    }

    private ReceiptDetails readDetails(Document document) {
        String docType = XmlUtils.evalXPath(document, "//codDoc").get();
        Optional<DocumentType> optionalDocumentEnum = DocumentType.getFromCode(docType);
        optionalDocumentEnum.orElseThrow(() -> new VeronicaException(format("El tipo de documento en [%s] es inválido", docType)));
        DocumentType documentType = optionalDocumentEnum.get();
        return ReceiptDetails.builder()
                .establishment(XmlUtils.evalXPath(document, "//estab").get())
                .emissionPoint(XmlUtils.evalXPath(document, "//ptoEmi").get())
                .docType(docType)
                .docNumber(XmlUtils.evalXPath(document, "//secuencial").get())
                .supplierNumber(XmlUtils.evalXPath(document, "//ruc").get())
                .customerNumber(XmlUtils.evalXPath(document, extractorFactory.get(documentType).getCustomerNumberXPath()).get())
                .documentType(documentType)
                .build();
    }

    private String getAccessKey(String responseBody, SriStatusType sriStatusType) {
        return sriStatusType == STATUS_APPLIED ? JsonPath.read(responseBody, "$.result.autorizaciones[0].numeroAutorizacion") :
                sriStatusType == STATUS_NOT_APPLIED ? JsonPath.read(responseBody, "$.result.claveAccesoConsultada") : "";
    }

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

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReceiptDetails {

    private String establishment;
    private String emissionPoint;
    private String docType;
    private String docNumber;
    private String supplierNumber;
    private String customerNumber;
    private DocumentType documentType;

}
