package ec.veronica.job.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class UsuarioInfoDTO implements Serializable {

    private UUID id;
    private String usuario;
    private String password;
    private String nombres;
    private String email;
    private String rol;
    private List<LicenciaDTO> licencias;

}
