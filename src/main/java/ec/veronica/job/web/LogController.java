package ec.veronica.job.web;

import ec.veronica.job.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @RequestMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("logs", logService.findAll());
        return "/logs";
    }

    @GetMapping(value = "api/logs/{log-id}/delete")
    public String deleteRoute(@PathVariable("log-id") Long logId) {
        logService.delete(logId);
        return "redirect:/logs";
    }

    @GetMapping(value = "api/logs/delete")
    public String deleteAllRoutes() {
        logService.deleteAll();
        return "redirect:/logs";
    }

}
