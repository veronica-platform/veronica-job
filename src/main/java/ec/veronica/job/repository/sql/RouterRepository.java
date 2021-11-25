package ec.veronica.job.repository.sql;

import ec.veronica.job.domain.Router;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouterRepository extends JpaRepository<Router, String> {

    Optional<Router> findFirstBySupplierNumberOrRootFolder(String supplierNumber, String rootFolder);

    @Modifying
    @Query("update Router router set router.enabled = :status where router.id = :routeId")
    void updateStatus(@Param("routeId") String routeId, @Param("status") boolean status);

    @Modifying
    @Query("update Router router set router.enabled = false")
    void disableAll();

}
