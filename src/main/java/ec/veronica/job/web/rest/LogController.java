package ec.veronica.job.web.rest;

import ec.veronica.job.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "api/logs/")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping(value = "{log-id}/delete")
    public String deleteRoute(@PathVariable("log-id") Long logId) {
        logService.delete(logId);
        return "redirect:/eventos";
    }

    @GetMapping(value = "delete")
    public String deleteAllRoutes() {
        logService.deleteAll();
        return "redirect:/eventos";
    }

}
