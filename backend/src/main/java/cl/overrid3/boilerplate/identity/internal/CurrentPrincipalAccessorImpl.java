package cl.overrid3.boilerplate.identity.internal;

import org.springframework.stereotype.Component;

import cl.overrid3.boilerplate.identity.CurrentPrincipal;
import cl.overrid3.boilerplate.identity.CurrentPrincipalAccessor;

@Component
class CurrentPrincipalAccessorImpl implements CurrentPrincipalAccessor {

	@Override
	public CurrentPrincipal require() {
		CurrentPrincipal principal = TenantContext.get();
		if (principal == null) {
			throw new IllegalStateException("No hay un principal de organización en esta petición");
		}
		return principal;
	}

}
