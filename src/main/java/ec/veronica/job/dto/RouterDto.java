package ec.veronica.job.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouterDto {

    private String routeId;

    @NotEmpty
    private String supplierNumber;

    @NotEmpty
    private String rootFolder;

    private boolean enabled = true;

    private BigInteger receiptsCount = BigInteger.ZERO;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getRouteId() {
        return routeId;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public BigInteger getReceiptsCount() {
        return receiptsCount;
    }

}
