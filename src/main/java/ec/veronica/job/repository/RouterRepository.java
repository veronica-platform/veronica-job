package ec.veronica.job.repository;

import ec.veronica.job.domain.Router;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouterRepository extends JpaRepository<Router, String> {
}
