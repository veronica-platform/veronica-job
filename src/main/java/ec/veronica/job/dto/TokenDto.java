package ec.veronica.job.dto;

import lombok.Data;

@Data
public class TokenDto {

    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;

}
