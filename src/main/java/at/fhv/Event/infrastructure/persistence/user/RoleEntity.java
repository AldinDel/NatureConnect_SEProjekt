package at.fhv.Event.infrastructure.persistence.user;

import jakarta.persistence.*;

@Entity
@Table(name = "role", schema = "nature_connect")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 50) private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
