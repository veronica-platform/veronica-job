package ec.veronica.job;

import ec.veronica.job.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@RequiredArgsConstructor
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class VeronicaJobApp implements CommandLineRunner {

    private final RouterService routerService;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Guayaquil"));
    }

    public static void main(String[] args) {
        SpringApplication.run(VeronicaJobApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        routerService.findAll().stream()
                .filter(route -> route.isEnabled())
                .forEach(route -> routerService.start(route));
    }

}
