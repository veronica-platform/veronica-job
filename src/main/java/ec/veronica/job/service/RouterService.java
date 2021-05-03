package ec.veronica.job.service;

import ec.veronica.job.dto.RouterDto;

public interface RouterService {

    RouterDto create(RouterDto routerDto);

    void remove(String routeId);

}
