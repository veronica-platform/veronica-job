package ec.veronica.job.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UsuarioDto implements Serializable {

    private String usuario;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String email;
    private String rol;

}
