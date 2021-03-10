package ec.veronica.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import static java.lang.String.format;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class VeronicaJobApp {

    @Value("${veronica.api.client-id}")
    private String clientId;

    @Value("${veronica.api.client-secret}")
    private String clientSecret;

    @Value("${veronica.api.username}")
    private String username;

    @Value("${veronica.api.password}")
    private String password;

    @Value("${veronica.api.url}")
    private String veronicaApiUrl;

    public static void main(String[] args) {
        SpringApplication.run(VeronicaJobApp.class, args);
    }

    @Bean
    public OAuth2RestTemplate auth2RestTemplate() {
        ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
        details.setUsername(username);
        details.setPassword(password);
        details.setAccessTokenUri(format(veronicaApiUrl, "oauth/token"));
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(details);
        return oAuth2RestTemplate;
    }

}
