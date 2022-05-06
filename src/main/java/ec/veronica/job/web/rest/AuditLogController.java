package ec.veronica.job.web.rest;

import ec.veronica.job.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @DeleteMapping(value = "{log-id}")
    public ResponseEntity<?> delete(@PathVariable("log-id") Long logId) {
        auditLogService.delete(logId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping(value = "all")
    public ResponseEntity<?> deleteAll() {
        auditLogService.deleteAll();
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
