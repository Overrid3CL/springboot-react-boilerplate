package cl.overrid3.boilerplate.notes;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.overrid3.boilerplate.notes.internal.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "notes")
public class NoteController {

	private final NoteService notes;

	public NoteController(NoteService notes) {
		this.notes = notes;
	}

	@GetMapping
	@Operation(operationId = "listNotes")
	public List<NoteResponse> list() {
		return notes.list();
	}

	@GetMapping("/{id}")
	@Operation(operationId = "getNote")
	public NoteResponse get(@PathVariable UUID id) {
		return notes.get(id);
	}

	@PostMapping
	@Operation(operationId = "createNote")
	public ResponseEntity<NoteResponse> create(@Valid @RequestBody CreateNoteRequest request) {
		NoteResponse created = notes.create(request);
		return ResponseEntity.created(URI.create("/api/notes/" + created.id())).body(created);
	}

	@PutMapping("/{id}")
	@Operation(operationId = "updateNote")
	public NoteResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateNoteRequest request) {
		return notes.update(id, request);
	}

	@DeleteMapping("/{id}")
	@Operation(operationId = "deleteNote")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		notes.delete(id);
		return ResponseEntity.noContent().build();
	}

}
