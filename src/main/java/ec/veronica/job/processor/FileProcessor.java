package ec.veronica.job.processor;

import ec.veronica.job.commons.XmlUtils;
import ec.veronica.job.domain.AuditLog;
import ec.veronica.job.factory.ExtractorFactory;
import ec.veronica.job.factory.ProcessorFactory;
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
import java.time.LocalDateTime;

import static ec.veronica.job.commons.Constants.XML_XPATH_COD_DOC;
import static ec.veronica.job.commons.Constants.XML_XPATH_DOC_NUMBER;
import static ec.veronica.job.commons.Constants.XML_XPATH_EMISSION_POINT;
import static ec.veronica.job.commons.Constants.XML_XPATH_ESTABLISHMENT;
import static ec.veronica.job.commons.Constants.XML_XPATH_SUPPLIER_NUMBER;
import static ec.veronica.job.commons.StringUtils.getSriStatus;
import static ec.veronica.job.commons.XmlUtils.evalXPath;
import static ec.veronica.job.types.SriStatusType.STATUS_INTERNAL_ERROR;

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
            String payload = exchange.getIn().getBody(String.class);
            String response = veronicaApiService.postAndApply(payload);
            Document dom = XmlUtils.fromStringToDocument(payload);
            log.debug(response);
            SriStatusType status = getSriStatus(response);
            logResponse(response, status, dom);
            log.debug("--> END VERONICA INTEGRATION");
        } catch (Exception ex) {
            log.error("[process]", ex);
            processorFactory.get(STATUS_INTERNAL_ERROR).process(exchange, null, null);
        }
    }

    private DocumentType resolveDocumentType(Document dom) throws XPathExpressionException {
        return DocumentType.getFromCode(evalXPath(dom, XML_XPATH_COD_DOC).orElse("")).orElse(DocumentType.FACTURA);
    }

    private AuditLog logResponse(String responseBody, SriStatusType status, Document dom) throws XPathExpressionException {
        AuditLog logEntry = AuditLog
                .builder()
                .establishment(evalXPath(dom, XML_XPATH_ESTABLISHMENT).orElse(""))
                .emissionPoint(evalXPath(dom, XML_XPATH_EMISSION_POINT).orElse(""))
                .receiptNumber(evalXPath(dom, XML_XPATH_DOC_NUMBER).orElse(""))
                .supplierNumber(evalXPath(dom, XML_XPATH_SUPPLIER_NUMBER).orElse(""))
                .docType(resolveDocumentType(dom).getDescription())
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
