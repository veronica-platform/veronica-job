package ec.veronica.job.repository.sql;

import ec.veronica.job.domain.Listener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListenerRepository extends JpaRepository<Listener, String> {

    Optional<Listener> findFirstBySupplierNumberOrRootFolder(String supplierNumber, String rootFolder);

    @Modifying
    @Query("update Listener listener set listener.enabled = false")
    void disableAll();

}
