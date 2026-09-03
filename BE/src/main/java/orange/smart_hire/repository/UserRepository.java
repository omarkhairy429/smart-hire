package orange.smart_hire.repository;

import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleIn(Collection<UserRole> roles);

    /** Find staff by role(s) filtered to a specific company (for tenant isolation). */
    List<User> findByRoleInAndCompanyName(Collection<UserRole> roles, String companyName);
}