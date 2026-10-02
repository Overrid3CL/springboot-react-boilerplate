package cl.overrid3.boilerplate.identity;

import java.util.UUID;

public record MeResponse(
		UUID userId,
		UUID organizationId,
		String externalSubject,
		String externalOrganizationId,
		String role) {
}
