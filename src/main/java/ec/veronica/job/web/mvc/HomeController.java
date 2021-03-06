package ec.veronica.job.web.mvc;

import ec.veronica.job.commons.SessionUtils;
import ec.veronica.job.service.ListenerService;
import ec.veronica.job.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ListenerService listenerService;
    private final SupplierService supplierService;

    @RequestMapping(value = {"", "/", "/procesos"})
    public String home(Model model) {
        model.addAttribute("user", SessionUtils.getCurrentUser().getUsuario());
        model.addAttribute("listeners", listenerService.findAll());
        return "index";
    }

    @RequestMapping("/procesos/crear")
    public String addProcess(Model model) {
        model.addAttribute("user", SessionUtils.getCurrentUser().getUsuario());
        model.addAttribute("suppliers", supplierService.findAll());
        return "listeners/create";
    }

}
