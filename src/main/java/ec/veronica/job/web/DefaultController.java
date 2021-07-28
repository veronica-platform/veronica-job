package ec.veronica.job.web;

import ec.veronica.job.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class DefaultController {

    private final RouterService routerService;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("routes", routerService.findAll());
        return "home";
    }

    @RequestMapping("login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("403")
    public String error403() {
        return "/error/403";
    }

}
