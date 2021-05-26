package ec.veronica.job.processor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.rolandopalermo.facturacion.ec.common.StringUtils;
import com.rolandopalermo.facturacion.ec.common.XmlUtils;
import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import com.rolandopalermo.facturacion.ec.common.types.DocumentType;
import ec.veronica.job.commons.Status;
import ec.veronica.job.config.ResourceOwnerPasswordResourceDetailsBuilder;
import ec.veronica.job.domain.Log;
import ec.veronica.job.dto.ReceiptDetailsDto;
import ec.veronica.job.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static ec.veronica.job.commons.Status.STATUS_INTERNAL_ERROR;
import static ec.veronica.job.commons.Status.STATUS_PENDING;
import static ec.veronica.job.commons.Status.STATUS_REJECTED;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileProcessor implements Processor {

    private final LogService logService;
    private final ResourceOwnerPasswordResourceDetailsBuilder resourceOwnerPasswordResourceDetailsBuilder;

    private OAuth2RestTemplate oAuth2RestTemplate;

    @Value("${veronica.api.url}")
    private String veronicaApiUrl;

    @Override
    public void process(Exchange exchange) {
        byte[] payload = exchange.getIn().getBody(String.class).getBytes(StandardCharsets.UTF_8);
        ReceiptDetailsDto receiptDetails;
        try {
            receiptDetails = readDetails(new String(payload));
        } catch (VeronicaException ex) {
            log(ex.getMessage(), STATUS_INTERNAL_ERROR.getValue(),
                    ReceiptDetailsDto
                            .builder()
                            .docNumber(exchange.getIn().getHeader("CamelFileName").toString())
                            .supplierNumber("-")
                            .docType("-")
                            .build());
            exchange.getIn().setHeader("status", STATUS_INTERNAL_ERROR.getValue());
            return;
        }
        log.debug("Sending receipt {}", receiptDetails.getDocNumber());
        String responseBody = sendReceipt(payload);
        log.debug("Receipt created {}", receiptDetails.getDocNumber());
        if (StringUtils.isNotEmpty(responseBody)) {
            Status status = getStatusFromResponse(responseBody);
            log(responseBody, status.getValue(), receiptDetails);
            String accessKey;
            switch (status) {
                case STATUS_INTERNAL_ERROR:
                    exchange.getIn().setHeader("status", STATUS_INTERNAL_ERROR.getValue());
                    return;
                case STATUS_REJECTED:
                    exchange.getIn().setHeader("status", STATUS_REJECTED.getValue());
                    return;
                case STATUS_APPLIED:
                    accessKey = JsonPath.read(responseBody, "$.result.autorizaciones[0].numeroAutorizacion");
                    exchange.getIn().setHeader("accessKey", accessKey);
                    exchange.getIn().setHeader("appliedInvoice", getReceiptFile(accessKey, "xml"));
                    exchange.getIn().setHeader("ride", getReceiptFile(accessKey, "pdf"));
                    break;
                case STATUS_NOT_APPLIED:
                    accessKey = JsonPath.read(responseBody, "$.result.claveAccesoConsultada");
                    exchange.getIn().setHeader("accessKey", accessKey);
                    exchange.getIn().setHeader("appliedInvoice", getReceiptFile(accessKey, "xml"));
                    break;
            }
            exchange.getIn().setHeader("status", status.getValue());
            exchange.getIn().setHeader("folderName", receiptDetails.getCustomerNumber());
            exchange.getIn().setHeader("fileName", format("%s-%s-%s-%s",
                    receiptDetails.getDocumentType().getShortName(),
                    receiptDetails.getEstab(),
                    receiptDetails.getPtoEmision(),
                    receiptDetails.getDocNumber()));
        } else {
            log(responseBody, STATUS_PENDING.getValue(), receiptDetails);
            exchange.getIn().setHeader("status", STATUS_PENDING.getValue());
        }
    }

    private Status getStatusFromResponse(String responseBody) {
        Optional<Status> status;
        try {
            status = Status.fromValue(JsonPath.read(responseBody, "$.result.autorizaciones[0].estado"));
        } catch (PathNotFoundException ex1) {
            try {
                status = Status.fromValue(JsonPath.read(responseBody, "$.result.estado"));
            } catch (PathNotFoundException ex2) {
                status = Optional.of(STATUS_INTERNAL_ERROR);
            }
        }
        return status.orElse(STATUS_PENDING);
    }

    private String sendReceipt(byte[] payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_ATOM_XML);
        HttpEntity<byte[]> entity = new HttpEntity<>(payload, headers);
        String responseBody = "";
        String url = format(veronicaApiUrl, "sri");
        try {
            ResponseEntity<String> result = getOAuth2RestTemplate().postForEntity(url, entity, String.class);
            responseBody = result.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            responseBody = ex.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("Error requesting the url {}", url);
        }
        return responseBody;
    }

    private byte[] getReceiptFile(String accessKey, String format) {
        try {
            String url = format(veronicaApiUrl, "comprobantes/%s/archivos?copia=true&format=%s");
            ResponseEntity<byte[]> result = getOAuth2RestTemplate().getForEntity(format(url, accessKey, format), byte[].class);
            return result.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("An error occurred trying to get the receipt file", ex);
            return new byte[0];
        }
    }

    private void log(String responseBody, String status, ReceiptDetailsDto receiptDetails) {
        Log log = Log
                .builder()
                .estab(receiptDetails.getEstab())
                .ptoEmision(receiptDetails.getPtoEmision())
                .receiptNumber(receiptDetails.getDocNumber())
                .docType(receiptDetails.getDocType())
                .response(responseBody)
                .status(status)
                .supplierNumber(receiptDetails.getSupplierNumber())
                .insertionDate(LocalDateTime.now())
                .build();
        logService.save(log);
    }

    private ReceiptDetailsDto readDetails(String xml) {
        try {
            Document document = XmlUtils.fromStringToDocument(xml);
            String docType = XmlUtils.evalXPath(document, "//codDoc").get();
            Optional<DocumentType> optionalDocumentEnum = DocumentType.getFromCode(docType);
            optionalDocumentEnum.orElseThrow(() -> new VeronicaException(format("El tipo de documento en %s es inválido", xml)));
            DocumentType documentType = optionalDocumentEnum.get();
            return ReceiptDetailsDto.builder()
                    .estab(XmlUtils.evalXPath(document, "//estab").get())
                    .ptoEmision(XmlUtils.evalXPath(document, "//ptoEmi").get())
                    .docType(docType)
                    .docNumber(XmlUtils.evalXPath(document, "//secuencial").get())
                    .supplierNumber(XmlUtils.evalXPath(document, "//ruc").get())
                    .customerNumber(XmlUtils.evalXPath(document, getCustomerNumberXPath(documentType)).get())
                    .documentType(documentType)
                    .build();
        } catch (VeronicaException ex) {
            throw ex;
        }
    }

    private String getCustomerNumberXPath(DocumentType documentType) {
        String customerNumberXpath;
        switch (documentType) {
            case FACTURA:
                customerNumberXpath = "/factura/infoFactura/identificacionComprador";
                break;
            case LIQUIDACION_COMPRAS:
                customerNumberXpath = "/liquidacionCompra/infoLiquidacionCompra/identificacionProveedor";
                break;
            case NOTA_CREDITO:
                customerNumberXpath = "/notaCredito/infoNotaCredito/identificacionComprador";
                break;
            case NOTA_DEBITO:
                customerNumberXpath = "/notaDebito/infoNotaDebito/identificacionComprador";
                break;
            case GUITA_REMISION:
                customerNumberXpath = "/guiaRemision/infoGuiaRemision/rucTransportista";
                break;
            case COMPROBANTE_RETENCION:
                customerNumberXpath = "/comprobanteRetencion/infoCompRetencion/identificacionSujetoRetenido";
                break;
            default:
                throw new VeronicaException("Tipo documento inválido");
        }
        return customerNumberXpath;
    }

    private OAuth2RestTemplate getOAuth2RestTemplate() {
        if (this.oAuth2RestTemplate == null) {
            oAuth2RestTemplate = new OAuth2RestTemplate(resourceOwnerPasswordResourceDetailsBuilder.build());
            oAuth2RestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            oAuth2RestTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
                public boolean hasError(ClientHttpResponse response) throws IOException {
                    HttpStatus statusCode = response.getStatusCode();
                    return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
                }
            });
            return oAuth2RestTemplate;
        }
        return this.oAuth2RestTemplate;
    }

}
