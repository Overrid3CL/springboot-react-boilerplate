package cl.overrid3.boilerplate.identity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/me")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "identity")
public class MeController {

	private final CurrentPrincipalAccessor principals;

	public MeController(CurrentPrincipalAccessor principals) {
		this.principals = principals;
	}

	@GetMapping
	@Operation(operationId = "getCurrentPrincipal")
	public MeResponse current() {
		CurrentPrincipal principal = principals.require();
		return new MeResponse(principal.userId(), principal.organizationId(), principal.externalSubject(),
				principal.externalOrganizationId(), principal.role());
	}

}
