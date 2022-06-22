package ec.veronica.job.repository.sql;

import ec.veronica.job.repository.sql.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = "select auditLog from AuditLog auditLog order by auditLog.insertionDate desc")
    List<AuditLog> findAll();

}
