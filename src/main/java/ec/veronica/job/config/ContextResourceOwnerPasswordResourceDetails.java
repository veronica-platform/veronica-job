package ec.veronica.job.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

@RequiredArgsConstructor
public class ContextResourceOwnerPasswordResourceDetails extends ResourceOwnerPasswordResourceDetails {

    private final String username;
    private final String password;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

}
