package ec.veronica.job.service;

import ec.veronica.job.domain.AuditLog;
import ec.veronica.job.repository.sql.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void save(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }

    public void delete(Long id) {
        auditLogRepository.deleteById(id);
    }

    public void deleteAll() {
        auditLogRepository.deleteAll();
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    public AuditLog findById(Long id) {
        return auditLogRepository.findById(id).orElse(new AuditLog());
    }

}
