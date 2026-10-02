package cl.overrid3.boilerplate.notes;

import java.time.Instant;
import java.util.UUID;

public record NoteResponse(UUID id, String title, String body, Instant createdAt, Instant updatedAt) {
}
