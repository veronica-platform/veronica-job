package ec.veronica.job.web.mvc;

import ec.veronica.job.commons.SessionUtils;
import ec.veronica.job.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("ApiKeyMvcController")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @RequestMapping(value = {"/api-keys"})
    public String list(Model model) {
        model.addAttribute("user", SessionUtils.getCurrentUser().getUsuario());
        model.addAttribute("apiKeys", apiKeyService.findAll());
        return "api-keys/home";
    }

    @RequestMapping("/api-keys/crear")
    public String addProcess(Model model) {
        model.addAttribute("user", SessionUtils.getCurrentUser().getUsuario());
        return "api-keys/create";
    }

}
