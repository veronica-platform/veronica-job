package ec.veronica.job.security;

import ec.veronica.job.dto.TokenDto;
import ec.veronica.job.service.VeronicaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final VeronicaApiService veronicaApiService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Login with credentials: {} and {}", authentication.getName(), authentication.getCredentials());
        String username = authentication.getName();
        TokenDto tokenDto = veronicaApiService.getToken(username, authentication.getCredentials().toString());
        if (tokenDto == null) {
            throw new BadCredentialsException(username);
        }
        return new UsernamePasswordAuthenticationToken(veronicaApiService.getUser(), null, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }

}
