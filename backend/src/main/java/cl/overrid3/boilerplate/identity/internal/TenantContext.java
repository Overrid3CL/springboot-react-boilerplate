package cl.overrid3.boilerplate.identity.internal;

import cl.overrid3.boilerplate.identity.CurrentPrincipal;

/**
 * Principal de la petición. Cada hilo virtual tiene el suyo y se limpia al terminar.
 */
final class TenantContext {

	private static final ThreadLocal<CurrentPrincipal> CURRENT = new ThreadLocal<>();

	private TenantContext() {
	}

	static void set(CurrentPrincipal principal) {
		CURRENT.set(principal);
	}

	static CurrentPrincipal get() {
		return CURRENT.get();
	}

	static void clear() {
		CURRENT.remove();
	}

}
