package ec.veronica.job.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LicenciaDTO implements Serializable {

    private UUID id;
    private String usuario;
    private String numeroRuc;
    private Boolean activa;
    private BigDecimal precio;
    private byte[] terminos;

}
