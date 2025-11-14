package at.fhv.Event.domain.model.user;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_account", schema = "nature_connect")
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 200) private String email;
    @Column(name="password_hash", nullable=false, length=255) private String passwordHash;
    private String firstName; private String lastName;
    @Column(nullable=false) private Boolean isActive = true;
    @Column(nullable=false) private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", schema="nature_connect",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}