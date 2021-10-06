package ec.veronica.job.service;

import ec.veronica.job.dto.RouterDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RouterService {

    void create(RouterDto routerDto);

    @Transactional
    void start(String routeId);

    @Transactional
    void stop(String routeId);

    @Transactional
    void delete(String routeId);

    void disableAll();

    List<RouterDto> findAll();

}
