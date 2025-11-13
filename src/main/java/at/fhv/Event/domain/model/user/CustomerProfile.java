package at.fhv.Event.domain.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "customer_profile", schema = "nature_connect")
public class CustomerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // nullable, 1:1 optional
    private UserAccount user;

    @Column(nullable=false, length=100) private String firstName;
    @Column(nullable=false, length=100) private String lastName;
    @Column(nullable=false, unique=true, length=200) private String email;
    @Column(length=50) private String phone;
    private LocalDate birthday;

    @Column(nullable=false) private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
}
