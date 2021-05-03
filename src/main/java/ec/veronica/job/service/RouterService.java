package ec.veronica.job.service;

import ec.veronica.job.dto.RouterDto;

import java.util.List;

public interface RouterService {

    RouterDto create(RouterDto routerDto);

    void remove(String routeId);

    List<RouterDto> findAll();

}
