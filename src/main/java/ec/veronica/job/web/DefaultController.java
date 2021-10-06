package ec.veronica.job.web;

import ec.veronica.job.http.VeronicaHttpClient;
import ec.veronica.job.service.LogService;
import ec.veronica.job.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class DefaultController {

    private final LogService logService;
    private final RouterService routerService;
    private final VeronicaHttpClient veronicaHttpClient;

    @RequestMapping(value = {"/", "/procesos"})
    public String home(Model model) {
        model.addAttribute("user", veronicaHttpClient.fetchUserInfo());
        model.addAttribute("routes", routerService.findAll());
        return "listProcess";
    }

    @RequestMapping("/procesos/crear")
    public String addProcess(Model model) {
        model.addAttribute("user", veronicaHttpClient.fetchUserInfo());
        return "addProcess";
    }

    @RequestMapping("/eventos")
    public String logs(Model model) {
        model.addAttribute("user", veronicaHttpClient.fetchUserInfo());
        model.addAttribute("logs", logService.findAll());
        return "logs";
    }

    @RequestMapping("/eventos/{log-id}/detalles")
    public String logDetails(@PathVariable("log-id") Long logId, Model model) {
        model.addAttribute("user", veronicaHttpClient.fetchUserInfo());
        model.addAttribute("log", logService.findById(logId));
        return "logDetails";
    }

    @RequestMapping("login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

}
