package cl.overrid3.boilerplate.notes.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.overrid3.boilerplate.identity.CurrentPrincipalAccessor;
import cl.overrid3.boilerplate.notes.CreateNoteRequest;
import cl.overrid3.boilerplate.notes.NoteResponse;
import cl.overrid3.boilerplate.notes.UpdateNoteRequest;

@Service
public class NoteService {

	private final NoteRepository notes;

	private final CurrentPrincipalAccessor principals;

	NoteService(NoteRepository notes, CurrentPrincipalAccessor principals) {
		this.notes = notes;
		this.principals = principals;
	}

	@Transactional(readOnly = true)
	public List<NoteResponse> list() {
		UUID organizationId = principals.require().organizationId();
		return notes.findByOrganizationIdOrderByCreatedAtDesc(organizationId).stream().map(NoteEntity::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public NoteResponse get(UUID id) {
		return findInCurrentOrganization(id).toResponse();
	}

	@Transactional
	public NoteResponse create(CreateNoteRequest request) {
		UUID organizationId = principals.require().organizationId();
		return notes.save(NoteEntity.create(organizationId, request.title().trim(), request.body().trim())).toResponse();
	}

	@Transactional
	public NoteResponse update(UUID id, UpdateNoteRequest request) {
		NoteEntity note = findInCurrentOrganization(id);
		note.update(request.title().trim(), request.body().trim());
		return note.toResponse();
	}

	@Transactional
	public void delete(UUID id) {
		notes.delete(findInCurrentOrganization(id));
	}

	private NoteEntity findInCurrentOrganization(UUID id) {
		UUID organizationId = principals.require().organizationId();
		return notes.findByIdAndOrganizationId(id, organizationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La nota no existe"));
	}

}
