package cl.overrid3.boilerplate.identity.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Datos públicos para que la SPA arme AuthKit en tiempo de ejecución, sin hornear el client id en la imagen.
 */
@RestController
@RequestMapping("/api/config")
@Tag(name = "config")
class PublicConfigController {

	private final WorkOsProperties properties;

	PublicConfigController(WorkOsProperties properties) {
		this.properties = properties;
	}

	@GetMapping
	@SecurityRequirements
	@Operation(operationId = "getPublicConfig")
	public PublicConfigResponse config() {
		return new PublicConfigResponse(properties.clientId(), blankToNull(properties.apiHostname()),
				blankToNull(properties.redirectUri()));
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value;
	}

}
