package cl.overrid3.boilerplate.identity.internal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<UserEntity, UUID> {

	Optional<UserEntity> findByExternalSubject(String externalSubject);

}
