package ec.veronica.job.repository.sql;

import ec.veronica.job.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    @Query(value = "select log from Log log order by log.insertionDate desc")
    List<Log> findAll();

}
