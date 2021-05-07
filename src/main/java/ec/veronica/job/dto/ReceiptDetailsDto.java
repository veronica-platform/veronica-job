package ec.veronica.job.dto;

import com.rolandopalermo.facturacion.ec.common.types.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailsDto {

    private String estab;
    private String ptoEmision;
    private String docType;
    private String docNumber;
    private String supplierNumber;
    private String customerNumber;
    private DocumentType documentType;

}
