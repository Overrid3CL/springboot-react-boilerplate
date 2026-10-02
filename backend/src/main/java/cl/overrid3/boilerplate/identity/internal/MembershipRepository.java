package cl.overrid3.boilerplate.identity.internal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface MembershipRepository extends JpaRepository<MembershipEntity, UUID> {

	Optional<MembershipEntity> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);

}
