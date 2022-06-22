package ec.veronica.job.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiKeyDto {

    private long id;
    private String keyValue;
    private LocalDateTime insertionDate;

}
