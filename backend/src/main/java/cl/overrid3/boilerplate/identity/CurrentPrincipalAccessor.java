package cl.overrid3.boilerplate.identity;

/**
 * Acceso al principal de la petición actual. Lo implementa el módulo de identidad.
 */
public interface CurrentPrincipalAccessor {

	CurrentPrincipal require();

}
