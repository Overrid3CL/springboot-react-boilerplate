package cl.overrid3.boilerplate.notes.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import cl.overrid3.boilerplate.notes.NoteResponse;

@Entity
@Table(name = "notes")
class NoteEntity {

	@Id
	private UUID id;

	@Column(name = "organization_id", nullable = false)
	private UUID organizationId;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "text")
	private String body;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected NoteEntity() {
	}

	static NoteEntity create(UUID organizationId, String title, String body) {
		Instant now = Instant.now();
		NoteEntity note = new NoteEntity();
		note.id = UUID.randomUUID();
		note.organizationId = organizationId;
		note.title = title;
		note.body = body;
		note.createdAt = now;
		note.updatedAt = now;
		return note;
	}

	void update(String title, String body) {
		this.title = title;
		this.body = body;
		this.updatedAt = Instant.now();
	}

	NoteResponse toResponse() {
		return new NoteResponse(id, title, body, createdAt, updatedAt);
	}

}
