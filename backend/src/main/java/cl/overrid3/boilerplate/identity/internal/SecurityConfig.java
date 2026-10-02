package cl.overrid3.boilerplate.identity.internal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * La SPA y la API comparten origen, así que no se habilita CORS.
 * La API es stateless y usa el access token como Bearer; por eso CSRF queda desactivado.
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(WorkOsProperties.class)
class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, IdentityProvisioningFilter provisioningFilter)
			throws Exception {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtAuthoritiesConverter());

		return http
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/api/config").permitAll()
						.requestMatchers("/api/**").authenticated()
						.requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.anyRequest().permitAll())
				.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
				.addFilterAfter(provisioningFilter, BearerTokenAuthenticationFilter.class)
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint((request, response, authException) -> SecurityProblemWriter.write(
								response,
								HttpStatus.UNAUTHORIZED,
								"No autorizado",
								"Se requiere un token de acceso válido"))
						.accessDeniedHandler((request, response, accessDeniedException) -> SecurityProblemWriter.write(
								response,
								HttpStatus.FORBIDDEN,
								"Prohibido",
								"No tienes permiso para esta operación")))
				.build();
	}

}
