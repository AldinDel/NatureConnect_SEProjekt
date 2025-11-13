package at.fhv.Event.domain.model.user;

import jakarta.persistence.*;

@Entity
@Table(name = "role", schema = "nature_connect")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 50) private String code; // 'ADMIN','FO_STAFF','FO_USER','ORGANIZER'
}
