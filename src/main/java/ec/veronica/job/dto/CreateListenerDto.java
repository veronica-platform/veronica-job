package ec.veronica.job.dto;

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
public class CreateListenerDto {

    @NotEmpty
    private String supplierNumber;

    @NotEmpty
    private String rootFolder;

    private boolean enabled = true;

}
