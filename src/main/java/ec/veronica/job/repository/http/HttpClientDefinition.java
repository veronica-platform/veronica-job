package ec.veronica.job.repository.http;

import ec.veronica.job.dto.TokenDto;
import ec.veronica.job.dto.UsuarioResponseDto;
import ec.veronica.job.dto.VeronicaResponseDto;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Repository;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Repository
public interface HttpClientDefinition {

    @POST("/api/v1.0/oauth/token")
    Call<TokenDto> getToken(
            @Query("username") String username,
            @Query("password") String password,
            @Query("grant_type") String grant_type,
            @Header("Authorization") String basicToken
    );

    @GET("/api/v1.0/usuarios/me")
    Call<VeronicaResponseDto<UsuarioResponseDto>> getUser(
            @Header("X-API-KEY") String apiKey
    );

    @GET("/api/v1.0/comprobantes/{accessKey}/archivos")
    Call<ResponseBody> getFile(
            @Header("X-API-KEY") String apiKey,
            @Path("accessKey") String accessKey,
            @Query("format") String format
    );

    @POST("/api/v1.0/sri")
    Call<String> postAndApply(
            @Header("X-API-KEY") String apiKey,
            @Body RequestBody requestBody
    );

}
