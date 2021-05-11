package ec.veronica.job.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Builder
@Table(name = "credentials")
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {

    @Id
    @Column(name = "username", unique = true, updatable = false, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

}
