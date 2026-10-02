package cl.overrid3.boilerplate.identity.internal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {

	Optional<OrganizationEntity> findByExternalId(String externalId);

}
