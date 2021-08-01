package ec.veronica.job.service;

import ec.veronica.job.dto.RouterDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public interface RouterService {

    RouterDto create(RouterDto routerDto, RedirectAttributes redirectAttributes);

    @Transactional
    void start(String routeId, RedirectAttributes redirectAttributes);

    @Transactional
    void stop(String routeId, RedirectAttributes redirectAttributes);

    @Transactional
    void delete(String routeId, RedirectAttributes redirectAttributes);

    void disableAll();

    List<RouterDto> findAll();

}
