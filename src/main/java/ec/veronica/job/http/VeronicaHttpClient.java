package ec.veronica.job.http;

import ec.veronica.job.config.ResourceOwnerPasswordResourceDetailsBuilder;
import ec.veronica.job.dto.UsuarioResponseDto;
import ec.veronica.job.dto.VeronicaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public UsuarioResponseDto getUserInfo() {
        return OAuth2RestTemplate().exchange(format(veronicaApiUrl, "usuarios/me"), HttpMethod.GET, null, new ParameterizedTypeReference<VeronicaResponseDTO<UsuarioResponseDto>>() { }).getBody().getResult();
    }

    public String postAndApply(byte[] encodedReceipt) {
        String responseBody = "";
        String url = format(veronicaApiUrl, "sri");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_ATOM_XML);
            HttpEntity<byte[]> entity = new HttpEntity<>(encodedReceipt, headers);
            return OAuth2RestTemplate().exchange(url, HttpMethod.POST, entity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            responseBody = ex.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("Error requesting the [{}]={}", url, ex.getMessage());
        }
        return responseBody;
    }

    public byte[] getFile(String accessKey, String format) {
        String url = format(veronicaApiUrl, "comprobantes/%s/archivos?copia=true&format=%s");
        return OAuth2RestTemplate().exchange(format(url, accessKey, format), HttpMethod.GET, null, byte[].class).getBody();
    }

    private OAuth2RestTemplate OAuth2RestTemplate() {
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
