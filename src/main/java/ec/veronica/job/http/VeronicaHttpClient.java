package ec.veronica.job.http;

import ec.veronica.job.config.ResourceOwnerPasswordResourceDetailsBuilder;
import ec.veronica.job.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.io.IOException;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class VeronicaHttpClient {

    @Value("${veronica.api.url}")
    private String veronicaApiUrl;

    private OAuth2RestTemplate oAuth2RestTemplate;

    private final ResourceOwnerPasswordResourceDetailsBuilder resourceOwnerPasswordResourceDetailsBuilder;

    public String sendReceipt(byte[] payload) {
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
            log.error("Error requesting the [{}]={}", url, ex.getMessage(), ex);
        }
        return responseBody;
    }

    public byte[] getReceiptFile(String accessKey, String format) {
        try {
            String url = format(veronicaApiUrl, "comprobantes/%s/archivos?copia=true&format=%s");
            ResponseEntity<byte[]> result = getOAuth2RestTemplate().getForEntity(format(url, accessKey, format), byte[].class);
            return result.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("An error occurred trying to get the receipt file: [{}]", ex.getMessage(), ex);
            return new byte[0];
        }
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
        }
        return this.oAuth2RestTemplate;
    }

}
