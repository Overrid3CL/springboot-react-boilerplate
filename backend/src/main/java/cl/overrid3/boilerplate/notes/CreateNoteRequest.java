package cl.overrid3.boilerplate.notes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(
		@NotBlank @Size(max = 200) String title,
		@NotBlank @Size(max = 10_000) String body) {
}
