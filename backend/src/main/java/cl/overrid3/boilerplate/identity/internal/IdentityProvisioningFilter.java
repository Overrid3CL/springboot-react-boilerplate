package cl.overrid3.boilerplate.identity.internal;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Después de validar el JWT, deja el tenant listo para el resto de la petición.
 */
@Component
class IdentityProvisioningFilter extends OncePerRequestFilter {

	private final IdentityProvisioningService provisioningService;

	IdentityProvisioningFilter(IdentityProvisioningService provisioningService) {
		this.provisioningService = provisioningService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication != null && authentication.getPrincipal() instanceof Jwt jwt)
				|| !request.getServletPath().startsWith("/api/")) {
			filterChain.doFilter(request, response);
			return;
		}

		String organizationId = jwt.getClaimAsString("org_id");
		if (organizationId == null || organizationId.isBlank()) {
			SecurityProblemWriter.write(response, HttpStatus.FORBIDDEN, "Organización requerida",
					"El token no incluye la organización activa");
			return;
		}

		try {
			TenantContext.set(provisioningService.provision(jwt));
			filterChain.doFilter(request, response);
		}
		finally {
			TenantContext.clear();
		}
	}

}
