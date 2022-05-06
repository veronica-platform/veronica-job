package ec.veronica.job.service;

import ec.veronica.job.dto.TokenDto;
import ec.veronica.job.dto.UsuarioResponseDto;
import ec.veronica.job.dto.VeronicaResponseDto;
import ec.veronica.job.exceptions.VeronicaException;
import ec.veronica.job.repository.http.HttpClientDefinition;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

@Slf4j
@Service
public class VeronicaApiService {

    @Value("${veronica.oauth.client-credentials}")
    private String basicToken;

    @Value("${veronica.api-key}")
    private String apiKey;

    private final Retrofit retrofit;

    private final Retrofit retrofitAsString;

    public VeronicaApiService(@Qualifier("retrofit") Retrofit retrofit, @Qualifier("retrofitAsString") Retrofit retrofitAsString) {
        this.retrofit = retrofit;
        this.retrofitAsString = retrofitAsString;
    }

    public TokenDto getToken(String username, String password) {
        try {
            Response<TokenDto> response = retrofit
                    .create(HttpClientDefinition.class)
                    .getToken(username, password, "password", "Basic " + basicToken)
                    .execute();
            if (!response.isSuccessful()) {
                handleError(response);
            }
            return response.body();
        } catch (Exception ex) {
            log.error("[getToken]", ex);
            throw new VeronicaException("Ocurrió un error un token de acceso válido");
        }
    }

    public UsuarioResponseDto getUser() {
        try {
            Response<VeronicaResponseDto<UsuarioResponseDto>> response = retrofit
                    .create(HttpClientDefinition.class)
                    .getUser(apiKey)
                    .execute();
            if (!response.isSuccessful()) {
                handleError(response);
            }
            return response.body().getResult();
        } catch (Exception ex) {
            log.error("[getUser]", ex);
            throw new VeronicaException("Ocurrió un error al obtener la información del usuario");
        }
    }

    public String postAndApply(String xml) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/atom+xml"), xml);
            Response<String> response = retrofitAsString
                    .create(HttpClientDefinition.class)
                    .postAndApply(apiKey, requestBody)
                    .execute();
            return response.body() != null ? response.body() : response.errorBody().string();
        } catch (Exception ex) {
            log.error("[postAndApply]", ex);
            throw new VeronicaException("Ocurrió un error al enviar el comprobante electrónico");
        }
    }

    private void handleError(Response response) throws IOException {
        throw new IOException(response.errorBody() != null
                ? response.errorBody().string() : "Unknown error");
    }

}
