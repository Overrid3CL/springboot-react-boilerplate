package cl.overrid3.boilerplate.identity.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/**
 * Configuración de WorkOS AuthKit. Todo llega por variables de entorno; no hay secretos en el repositorio.
 */
@Validated
@ConfigurationProperties(prefix = "app.workos")
public record WorkOsProperties(
		@NotBlank String clientId,
		@NotBlank String issuer,
		String audience,
		String jwksUri,
		String apiHostname,
		String redirectUri) {

	public String resolvedJwksUri() {
		if (jwksUri != null && !jwksUri.isBlank()) {
			return jwksUri;
		}
		return "https://api.workos.com/sso/jwks/" + clientId;
	}

	/**
	 * Si no hay audiencia explícita, se usa el client id. Los tokens de sesión de AuthKit
	 * no traen {@code aud} por defecto y se validan con el claim {@code client_id}.
	 */
	public String resolvedAudience() {
		if (audience != null && !audience.isBlank()) {
			return audience;
		}
		return clientId;
	}

	public boolean audienceRequired() {
		return audience != null && !audience.isBlank();
	}

}
