package ec.veronica.job.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class RetrofitConfig {

    @Value("${veronica.base-url}")
    private String veronicaBaseUrl;

    private final ObjectMapper objectMapper;

    @Bean
    @Qualifier("retrofit")
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(veronicaBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(okHttpClient())
                .build();
    }

    @Bean
    @Qualifier("retrofitAsString")
    public Retrofit retrofitAsString() {
        return new Retrofit.Builder()
                .baseUrl(veronicaBaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient())
                .build();
    }

    private OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

}
