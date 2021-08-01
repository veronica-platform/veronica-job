package ec.veronica.job.web;

import ec.veronica.job.dto.RouterDto;
import ec.veronica.job.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RouterController {

    private final RouterService routerService;

    @PostMapping(value = "api/routes")
    public String addRoute(RouterDto router, RedirectAttributes redirectAttributes) {
        routerService.create(router, redirectAttributes);
        return "redirect:/";
    }

    @GetMapping(value = "api/routes/{route-id}/delete")
    public String deleteRoute(@PathVariable("route-id") String routeId, RedirectAttributes redirectAttributes) {
        routerService.delete(routeId, redirectAttributes);
        return "redirect:/";
    }

    @GetMapping(value = "api/routes/{route-id}/start")
    public String start(@PathVariable("route-id") String routeId, RedirectAttributes redirectAttributes) {
        routerService.start(routeId, redirectAttributes);
        return "redirect:/";
    }

    @GetMapping(value = "api/routes/{route-id}/stop")
    public String stop(@PathVariable("route-id") String routeId, RedirectAttributes redirectAttributes) {
        routerService.stop(routeId, redirectAttributes);
        return "redirect:/";
    }

}
