package ec.veronica.job.commons;

import ec.veronica.job.dto.UsuarioResponseDto;
import ec.veronica.job.exceptions.VeronicaException;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SessionUtils {

    public static UsuarioResponseDto getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                return (UsuarioResponseDto) authentication.getPrincipal();
            }
        }
        throw new VeronicaException("La sesión es inválida");
    }

}
