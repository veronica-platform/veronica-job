package ec.veronica.job.config;

import com.rolandopalermo.facturacion.ec.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Value("${veronica.api.client-id}")
    private String clientId;

    @Value("${veronica.api.client-secret}")
    private String clientSecret;

    @Value("${veronica.api.grant-type}")
    private String scope;

    @Value("${veronica.api.url}")
    private String veronicaApiUrl;

    private final ResourceOwnerPasswordAccessTokenProvider provider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (!username.isEmpty() && !password.isEmpty()) {
            obtainToken(username, password);
            return new UsernamePasswordAuthenticationToken(username, authentication.getCredentials(), Collections.emptyList());
        } else {
            throw new UnauthorizedException(username);
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }

    private OAuth2AccessToken obtainToken(String username, String password) {
        ResourceOwnerPasswordResourceDetails passwordResourceDetails = new ResourceOwnerPasswordResourceDetails();
        passwordResourceDetails.setUsername(username);
        passwordResourceDetails.setPassword(password);
        passwordResourceDetails.setClientId(clientId);
        passwordResourceDetails.setClientSecret(clientSecret);
        passwordResourceDetails.setAccessTokenUri(format(veronicaApiUrl, "oauth/token"));
        DefaultAccessTokenRequest defaultAccessTokenRequest = new DefaultAccessTokenRequest();
        OAuth2AccessToken token;
        try {
            token = provider.obtainAccessToken(passwordResourceDetails, defaultAccessTokenRequest);
        } catch (OAuth2AccessDeniedException accessDeniedException) {
            log.error("obtainToken", accessDeniedException);
            throw new UnauthorizedException(username);
        }
        return token;
    }

}
