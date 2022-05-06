package ec.veronica.job;

import ec.veronica.job.service.ListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableWebSecurity
@RequiredArgsConstructor
@SpringBootApplication
public class VeronicaJobApp implements CommandLineRunner {

    private final ListenerService listenerService;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Guayaquil"));
    }

    public static void main(String[] args) {
        SpringApplication.run(VeronicaJobApp.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) {
        listenerService.disableAll();
    }

}
