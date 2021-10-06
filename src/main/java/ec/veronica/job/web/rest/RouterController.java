package ec.veronica.job.web.rest;

import ec.veronica.job.dto.RouterDto;
import ec.veronica.job.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "api/routes/")
@RequiredArgsConstructor
public class RouterController {

    private final RouterService routerService;

    @GetMapping(value = "{route-id}/delete")
    public String delete(@PathVariable("route-id") String routeId) {
        routerService.delete(routeId);
        return "redirect:/";
    }

    @GetMapping(value = "{route-id}/start")
    public String start(@PathVariable("route-id") String routeId) {
        routerService.start(routeId);
        return "redirect:/";
    }

    @GetMapping(value = "{route-id}/stop")
    public String stop(@PathVariable("route-id") String routeId) {
        routerService.stop(routeId);
        return "redirect:/";
    }

    @PostMapping
    public String add(RouterDto router) {
        routerService.create(router);
        return "redirect:/";
    }

}
