package cl.overrid3.boilerplate.identity;

import java.util.UUID;

/**
 * Usuario ya resuelto contra las tablas locales, con la organización activa del token.
 */
public record CurrentPrincipal(
		UUID userId,
		UUID organizationId,
		String externalSubject,
		String externalOrganizationId,
		String role) {
}
