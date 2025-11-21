package at.fhv.Event.domain.model.user;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "organizer", schema = "nature_connect")
public class Organizer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(nullable=false, length=200) private String name;
    @Column(unique=true, length=200) private String email;
    @Column(length=50) private String phone;
    @Column(nullable=false) private Boolean isActive = true;

    @Column(nullable=false) private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
}
