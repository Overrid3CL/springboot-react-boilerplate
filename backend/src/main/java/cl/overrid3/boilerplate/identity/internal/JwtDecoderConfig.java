package cl.overrid3.boilerplate.identity.internal;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Valida la firma con el JWKS de WorkOS y comprueba emisor y audiencia.
 */
@Configuration
class JwtDecoderConfig {

	@Bean
	JwtDecoder jwtDecoder(WorkOsProperties properties) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(properties.resolvedJwksUri()).build();
		OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(List.of(
				new JwtTimestampValidator(),
				new JwtIssuerValidator(properties.issuer()),
				new WorkOsAudienceValidator(properties)));
		decoder.setJwtValidator(validator);
		return decoder;
	}

}
