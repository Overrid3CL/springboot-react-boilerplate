package cl.overrid3.boilerplate.notes.internal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface NoteRepository extends JpaRepository<NoteEntity, UUID> {

	List<NoteEntity> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);

	Optional<NoteEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

}
