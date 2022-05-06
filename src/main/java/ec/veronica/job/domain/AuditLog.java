package ec.veronica.job.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    @GeneratedValue
    private Long id;

    @Column(name = "supplier_number")
    private String supplierNumber;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "establishment")
    private String establishment;

    @Column(name = "emission_point")
    private String emissionPoint;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "doc_code")
    private String docCode;

    @Column(name = "doc_type")
    private String docTypeName;

    @Column(name = "status")
    private String status;

    @Column(name = "response", length = 1000)
    private String response;

    @Column(name = "insertion_date")
    private LocalDateTime insertionDate;

}
