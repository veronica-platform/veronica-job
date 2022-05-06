package ec.veronica.job.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "listener")
public class Listener {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false)
    private String id;

    @Column(name = "supplier_number")
    private String supplierNumber;

    @Column(name = "root_folder")
    private String rootFolder;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "receipts_count")
    private BigInteger receiptsCount;

}
