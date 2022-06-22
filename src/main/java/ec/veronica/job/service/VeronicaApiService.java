package ec.veronica.job.service;

import ec.veronica.job.dto.ApiKeyDto;
import ec.veronica.job.dto.SupplierDto;
import ec.veronica.job.dto.TokenDto;
import ec.veronica.job.dto.UsuarioResponseDto;
import ec.veronica.job.dto.VeronicaResponseDto;
import ec.veronica.job.exceptions.VeronicaException;
import ec.veronica.job.repository.http.HttpClientDefinition;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class VeronicaApiService {

    @Value("${veronica.oauth.client-credentials}")
    private String basicToken;

    private final Retrofit retrofit;
    private final Retrofit retrofitAsString;
    private final ApiKeyService apiKeyService;

    public VeronicaApiService(
            @Qualifier("retrofit") Retrofit retrofit,
            @Qualifier("retrofitAsString") Retrofit retrofitAsString,
            ApiKeyService apiKeyService) {
        this.retrofit = retrofit;
        this.retrofitAsString = retrofitAsString;
        this.apiKeyService = apiKeyService;
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

    public UsuarioResponseDto getUser(String bearerToken) {
        try {
            Response<VeronicaResponseDto<UsuarioResponseDto>> response = retrofit
                    .create(HttpClientDefinition.class)
                    .getUser("Bearer " + bearerToken)
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

    public byte[] getFile(String accessKey, String format) {
        try {
            Response<ResponseBody> response = retrofit
                    .create(HttpClientDefinition.class)
                    .getFile(getApiKey(), accessKey, format)
                    .execute();
            if (!response.isSuccessful()) {
                handleError(response);
            }
            return response.body().bytes();
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
                    .postAndApply(getApiKey(), requestBody)
                    .execute();
            return response.body() != null ? response.body() : response.errorBody().string();
        } catch (Exception ex) {
            log.error("[postAndApply]", ex);
            throw new VeronicaException("Ocurrió un error al enviar el comprobante electrónico");
        }
    }

    public List<SupplierDto> findAllSuppliers(String bearerToken) {
        try {
            Response<VeronicaResponseDto<List<SupplierDto>>> response = retrofit
                    .create(HttpClientDefinition.class)
                    .findAllSuppliers("Bearer " + bearerToken)
                    .execute();
            return response.body() != null ? response.body().getResult() : Collections.emptyList();
        } catch (Exception ex) {
            log.error("[findAllSuppliers]", ex);
            throw new VeronicaException("Ocurrió un error al obtener la lista de empresas del usuario");
        }
    }

    private String getApiKey() {
        List<ApiKeyDto> apiKeys = apiKeyService.findAll();
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new VeronicaException("No existe una API Key válida");
        }
        return apiKeys.get(0).getKeyValue();
    }

    private void handleError(Response response) throws IOException {
        throw new IOException(response.errorBody() != null
                ? response.errorBody().string() : "Unknown error");
    }

}
