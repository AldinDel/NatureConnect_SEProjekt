package at.fhv.Event.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, Long> {
    Optional<UserAccountEntity> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "roles")
    List<UserAccountEntity> findTop5ByOrderByIdDesc();

    @Query(value = """
            select distinct ua.*
            from nature_connect.user_account ua
                left join nature_connect.user_role ur on ur.user_id = ua.id
                left join nature_connect.role r on r.id = ur.role_id
            
            where (:role is null or r.code = :role)
              and (
                   :q is null
                   or cast(ua.first_name as text) ilike concat('%', :q, '%')
                   or cast(ua.last_name as text) ilike concat('%', :q, '%')
                   or cast(ua.email as text) ilike concat('%', :q, '%')
                   or (:idExact is not null and ua.id = :idExact)
              )
            order by ua.id desc
            """,
            nativeQuery = true)
    List<UserAccountEntity> searchAdminUsers(
            @Param("q") String q,
            @Param("idExact") Long idExact,
            @Param("role") String role,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "roles")
    List<UserAccountEntity> findAllByIdIn(List<Long> ids);

}
