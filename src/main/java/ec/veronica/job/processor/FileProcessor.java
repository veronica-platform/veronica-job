package ec.veronica.job.processor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static ec.veronica.job.commons.XmlUtils.xpath;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class FileProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);
    private final OAuth2RestTemplate auth2RestTemplate;

    @Value("${veronica.api.url}")
    private String veronicaApiUrl;

    @PostConstruct
    public void init() {
        auth2RestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        auth2RestTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = response.getStatusCode();
                return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
            }
        });
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String xml = exchange.getIn().getBody(String.class);

        String docType = xpath(xml, "//codDoc");
        String docNumber = xpath(xml, "//secuencial");
        String customerNumber = xpath(xml, getCustomerNumberXPath(docType));

        LOGGER.debug("Se inicia envío de comprobante {}", docNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_ATOM_XML);
        HttpEntity<String> entity = new HttpEntity<>(xml, headers);
        String responseBody = "";
        try {
            ResponseEntity<String> result = auth2RestTemplate.postForEntity(format(veronicaApiUrl, "sri"), entity, String.class);
            responseBody = result.getBody();
            LOGGER.debug(responseBody);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOGGER.error(ex.getResponseBodyAsString());
            exchange.getIn().setHeader("status", "CON_ERRORES");
            return;
        }

        String status = "";
        try {
            status = JsonPath.read(responseBody, "$.result.autorizaciones[0].estado");
        } catch (PathNotFoundException e1) {
            try {
                status = JsonPath.read(responseBody, "$.result.estado");
            } catch (PathNotFoundException e2) {
                exchange.getIn().setHeader("status", "CON_ERRORES");
                LOGGER.debug(responseBody);
                return;
            }
        }

        if (status.compareTo("AUTORIZADO") == 0) {
            String accessKey = JsonPath.read(responseBody, "$.result.autorizaciones[0].numeroAutorizacion");
            exchange.getIn().setHeader("accessKey", accessKey);
            exchange.getIn().setHeader("appliedInvoice", getReceiptFile(accessKey, "xml"));
            exchange.getIn().setHeader("ride", getReceiptFile(accessKey, "pdf"));
        }

        if (status.compareTo("NO AUTORIZADO") == 0) {
            System.err.println(responseBody);
            String accessKey = JsonPath.read(responseBody, "$.result.claveAccesoConsultada");
            exchange.getIn().setHeader("accessKey", accessKey);
            exchange.getIn().setHeader("appliedInvoice", getReceiptFile(accessKey, "xml"));
        }

        exchange.getIn().setHeader("status", status);
        exchange.getIn().setHeader("folderName", customerNumber);
    }

    private byte[] getReceiptFile(String accessKey, String format) {
        try {
            String url = format(veronicaApiUrl, "comprobantes/%s/archivos?copia=true&format=%s");
            ResponseEntity<byte[]> result = auth2RestTemplate.getForEntity(format(url, accessKey, format), byte[].class);
            return result.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOGGER.error(ex.getResponseBodyAsString());
            return null;
        }
    }

    private String getCustomerNumberXPath(String codDoc) {
        String customerNumberXpath;
        switch (codDoc) {
            case "01":
                customerNumberXpath = "/factura/infoFactura/identificacionComprador";
                break;
            case "03":
                customerNumberXpath = "/liquidacionCompra/infoLiquidacionCompra/identificacionProveedor";
                break;
            case "04":
                customerNumberXpath = "/notaCredito/infoNotaCredito/identificacionComprador";
                break;
            case "05":
                customerNumberXpath = "/notaDebito/infoNotaDebito/identificacionComprador";
                break;
            case "06":
                customerNumberXpath = "/guiaRemision/infoGuiaRemision/rucTransportista";
                break;
            case "07":
                customerNumberXpath = "/comprobanteRetencion/infoCompRetencion/identificacionSujetoRetenido";
                break;
            default:
                throw new RuntimeException("Tipo documento inválido");
        }
        return customerNumberXpath;
    }

}
