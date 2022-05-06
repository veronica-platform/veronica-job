package ec.veronica.job.web.rest;

import ec.veronica.job.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "api/audit-logs/")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping(value = "{log-id}/delete")
    public String deleteRoute(@PathVariable("log-id") Long logId) {
        auditLogService.delete(logId);
        return "redirect:/eventos";
    }

    @GetMapping(value = "delete")
    public String deleteAllRoutes() {
        auditLogService.deleteAll();
        return "redirect:/eventos";
    }

}
