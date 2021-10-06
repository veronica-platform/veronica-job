package ec.veronica.job.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RouterDto {

    private String routeId;

    @NotEmpty
    private String supplierNumber;

    @NotEmpty
    private String rootFolder;

    private boolean enabled = true;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getRouteId() {
        return routeId;
    }

}
