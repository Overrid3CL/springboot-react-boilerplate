package cl.overrid3.boilerplate.identity.internal;

import java.util.List;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Valida que el JWT pertenezca a esta aplicación.
 * Con {@code WORKOS_AUDIENCE} se exige el claim {@code aud}. Si no, basta el {@code client_id}
 * de AuthKit, que es el identificador de la aplicación cuando el token de sesión no trae audiencia.
 */
final class WorkOsAudienceValidator implements OAuth2TokenValidator<Jwt> {

	private final WorkOsProperties properties;

	WorkOsAudienceValidator(WorkOsProperties properties) {
		this.properties = properties;
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt token) {
		String clientId = properties.clientId();
		String tokenClientId = token.getClaimAsString("client_id");
		boolean clientMatches = clientId.equals(tokenClientId);

		List<String> audience = token.getAudience();
		boolean hasAudience = audience != null && !audience.isEmpty();
		boolean audienceMatches = hasAudience && audience.contains(properties.resolvedAudience());

		if (properties.audienceRequired()) {
			if (!audienceMatches) {
				return failure("El token no incluye la audiencia configurada");
			}
			if (tokenClientId != null && !clientMatches) {
				return failure("El client_id del token no corresponde a esta aplicación");
			}
			return OAuth2TokenValidatorResult.success();
		}

		if (clientMatches || audienceMatches) {
			return OAuth2TokenValidatorResult.success();
		}
		return failure("El token no corresponde a esta aplicación");
	}

	private static OAuth2TokenValidatorResult failure(String description) {
		return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", description, null));
	}

}
