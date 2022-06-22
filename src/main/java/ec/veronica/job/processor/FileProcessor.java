package ec.veronica.job.processor;

import ec.veronica.job.commons.XmlUtils;
import ec.veronica.job.factory.ExtractorFactory;
import ec.veronica.job.factory.ProcessorFactory;
import ec.veronica.job.repository.sql.entity.AuditLog;
import ec.veronica.job.service.AuditLogService;
import ec.veronica.job.service.VeronicaApiService;
import ec.veronica.job.types.DocumentType;
import ec.veronica.job.types.SriStatusType;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static ec.veronica.job.commons.Constants.XML_XPATH_COD_DOC;
import static ec.veronica.job.commons.Constants.XML_XPATH_DOC_NUMBER;
import static ec.veronica.job.commons.Constants.XML_XPATH_EMISSION_POINT;
import static ec.veronica.job.commons.Constants.XML_XPATH_ESTABLISHMENT;
import static ec.veronica.job.commons.Constants.XML_XPATH_SUPPLIER_NUMBER;
import static ec.veronica.job.commons.StringUtils.getAccessKey;
import static ec.veronica.job.commons.StringUtils.getSriStatus;
import static ec.veronica.job.commons.XmlUtils.evalXPath;
import static ec.veronica.job.types.SriStatusType.STATUS_APPLIED;
import static ec.veronica.job.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static ec.veronica.job.types.SriStatusType.STATUS_NOT_APPLIED;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileProcessor implements Processor {

    private final AuditLogService auditLogService;
    private final ExtractorFactory extractorFactory;
    private final ProcessorFactory processorFactory;
    private final VeronicaApiService veronicaApiService;

    @Override
    public void process(Exchange exchange) {
        try {
            log.debug("--> START VERONICA INTEGRATION");
            String request = exchange.getIn().getBody(String.class);
            String response = veronicaApiService.postAndApply(request);
            log.debug(response);
            SriStatusType status = getSriStatus(response);
            AuditLog auditLog = logResponse(request, response, status);
            String accessKey = getAccessKey(response, status);
            byte[] pdf = status == STATUS_APPLIED ? veronicaApiService.getFile(accessKey, "pdf") : null;
            byte[] xml = status == STATUS_APPLIED || status == STATUS_NOT_APPLIED ?
                    veronicaApiService.getFile(accessKey, "xml") :
                    request.getBytes(StandardCharsets.UTF_8);
            processorFactory.get(status).process(exchange, pdf, xml);
            exchange.getIn().setHeader("folderName", auditLog.getCustomerNumber());
            exchange.getIn().setHeader("fileName", getFileName(auditLog));
            log.debug("--> END VERONICA INTEGRATION");
        } catch (Exception ex) {
            log.error("[process]", ex);
            processorFactory.get(STATUS_INTERNAL_ERROR).process(exchange, null, null);
        }
    }

    private String getFileName(AuditLog auditLog) {
        return format("%s-%s-%s-%s",
                DocumentType.getFromCode(auditLog.getDocCode()).get().getShortName(),
                auditLog.getEstablishment(),
                auditLog.getEmissionPoint(),
                auditLog.getReceiptNumber()
        );
    }

    private DocumentType resolveDocumentType(Document dom) throws XPathExpressionException {
        return DocumentType.getFromCode(evalXPath(dom, XML_XPATH_COD_DOC).orElse("")).orElse(DocumentType.FACTURA);
    }

    private AuditLog logResponse(String request, String responseBody, SriStatusType status) throws Exception {
        Document dom = XmlUtils.fromStringToDocument(request);
        DocumentType documentType = resolveDocumentType(dom);
        AuditLog logEntry = AuditLog
                .builder()
                .establishment(evalXPath(dom, XML_XPATH_ESTABLISHMENT).orElse(""))
                .emissionPoint(evalXPath(dom, XML_XPATH_EMISSION_POINT).orElse(""))
                .receiptNumber(evalXPath(dom, XML_XPATH_DOC_NUMBER).orElse(""))
                .supplierNumber(evalXPath(dom, XML_XPATH_SUPPLIER_NUMBER).orElse(""))
                .customerNumber(evalXPath(dom, extractorFactory.get(documentType).getCustomerNumberXPath()).orElse(""))
                .docCode(documentType.getCode())
                .docTypeName(documentType.getDescription())
                .response(responseBody)
                .status(status.getValue())
                .insertionDate(LocalDateTime.now())
                .build();
        return auditLogService.save(logEntry);
    }

}

@Data
@Builder
class ReceiptDetails {
    String establishment;
    String emissionPoint;
    String documentNumber;
    DocumentType documentType;
    String supplierNumber;
}
