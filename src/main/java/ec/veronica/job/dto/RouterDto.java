package ec.veronica.job.dto;

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

    @NotEmpty
    private String supplierNumber;
    @NotEmpty
    private String rootFolder;
    private boolean isEnabled;

}
