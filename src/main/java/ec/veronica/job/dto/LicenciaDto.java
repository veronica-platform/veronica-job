package ec.veronica.job.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LicenciaDto implements Serializable {

    private UUID id;
    private String numeroRuc;
    private String razonSocial;
    private String fechaVencimiento;
    private BigDecimal precio;
    private boolean activa;

}
