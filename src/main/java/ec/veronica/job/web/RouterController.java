package ec.veronica.job.web;

import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
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

    @GetMapping(value = "api/routes/{route-id}/delete")
    public String deleteRoute(RedirectAttributes redirectAttrs, @PathVariable("route-id") String routeId) {
        try {
            routerService.remove(routeId);
        } catch (VeronicaException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
        }
        return redirectHome(redirectAttrs);
    }

    @GetMapping(value = "api/routes/{route-id}/enable")
    public String enable(RedirectAttributes redirectAttrs, @PathVariable("route-id") String routeId) {
        try {
            routerService.start(routeId);
        } catch (VeronicaException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
        }
        return redirectHome(redirectAttrs);
    }

    @GetMapping(value = "api/routes/{route-id}/disable")
    public String disable(RedirectAttributes redirectAttrs, @PathVariable("route-id") String routeId) {
        try {
            routerService.stop(routeId);
        } catch (VeronicaException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
        }
        return redirectHome(redirectAttrs);
    }

    @PostMapping(value = "api/routes")
    public String addRoute(RouterDto router, RedirectAttributes redirectAttrs) {
        try {
            routerService.create(router);
        } catch (VeronicaException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
        }
        return redirectHome(redirectAttrs);
    }

    private String redirectHome(RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("routes", routerService.findAll());
        return "redirect:/";
    }

}
