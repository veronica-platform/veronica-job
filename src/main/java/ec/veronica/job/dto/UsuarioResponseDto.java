package ec.veronica.job.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UsuarioResponseDto implements Serializable {

    private UsuarioDto usuario;
    //private List<LicenciaDto> licencias;

}
