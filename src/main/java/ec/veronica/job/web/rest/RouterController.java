package ec.veronica.job.web.rest;

import ec.veronica.job.dto.RouterDto;
import ec.veronica.job.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RouterController {

    private final RouterService routerService;

    @GetMapping(value = "api/routes/{route-id}/delete")
    public String deleteRoute(@PathVariable("route-id") String routeId) {
        routerService.remove(routeId);
        return "redirect:/";
    }

    @GetMapping(value = "api/routes/{route-id}/enable")
    public String enable(@PathVariable("route-id") String routeId) {
        routerService.start(routeId);
        return "redirect:/";
    }

    @GetMapping(value = "api/routes/{route-id}/disable")
    public String disable(@PathVariable("route-id") String routeId) {
        routerService.stop(routeId);
        return "redirect:/";
    }

    @PostMapping(value = "api/routes")
    public String addRoute(Model model, RouterDto router) {
        try {
            routerService.create(router);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }

}
