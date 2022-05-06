package ec.veronica.job.web.mvc;

import ec.veronica.job.commons.SessionUtils;
import ec.veronica.job.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class EventsController {

    private final AuditLogService auditLogService;

    @RequestMapping(value = {"/eventos"})
    public String list(Model model) {
        model.addAttribute("user", SessionUtils.getCurrentUser().getUsuario());
        model.addAttribute("logs", auditLogService.findAll());
        return "events/home";
    }

    @RequestMapping("/eventos/{log-id}/detalles")
    public String logDetails(@PathVariable("log-id") Long logId, Model model) {
        model.addAttribute("user", SessionUtils.getCurrentUser().getUsuario());
        model.addAttribute("log", auditLogService.findById(logId));
        return "events/details";
    }

}
