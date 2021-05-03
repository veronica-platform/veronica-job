package ec.veronica.job.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

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
    private boolean isEnabled;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getRouteId() {
        return routeId;
    }

    @Override
    public String toString() {
        return "RouterDto{" +
                "supplierNumber='" + supplierNumber + '\'' +
                ", rootFolder='" + rootFolder + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }

}
