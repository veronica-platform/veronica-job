package ec.veronica.job.config;

import ec.veronica.common.exception.VeronicaException;
import ec.veronica.job.domain.Credentials;
import ec.veronica.job.repository.sql.CredentialsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Data
@Component
@RequiredArgsConstructor
public class ResourceOwnerPasswordResourceDetailsBuilder {

    @Value("${veronica.api.client-id}")
    private String clientId;

    @Value("${veronica.api.client-secret}")
    private String clientSecret;

    @Value("${veronica.api.grant-type}")
    private String grantType;

    @Value("${veronica.api.url}")
    private String veronicaApiUrl;

    private final CredentialsRepository credentialsRepository;

    public ContextResourceOwnerPasswordResourceDetails build() {
        Credentials credentials = getCredentials();
        ContextResourceOwnerPasswordResourceDetails resourceOwnerPasswordResourceDetails = new ContextResourceOwnerPasswordResourceDetails(
                credentials.getUsername(),
                credentials.getPassword()
        );
        resourceOwnerPasswordResourceDetails.setGrantType(grantType);
        resourceOwnerPasswordResourceDetails.setAccessTokenUri(format(veronicaApiUrl, "oauth/token"));
        resourceOwnerPasswordResourceDetails.setClientId(clientId);
        resourceOwnerPasswordResourceDetails.setClientSecret(clientSecret);
        return resourceOwnerPasswordResourceDetails;
    }

    private Credentials getCredentials() {
        return credentialsRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new VeronicaException("No se encontraron credenciales válidas para ejecutar la petición"));
    }

}
