package cl.overrid3.boilerplate.identity.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Traduce el rol y los permisos del JWT de WorkOS a authorities de Spring Security.
 */
final class JwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		addRole(authorities, jwt.getClaimAsString("role"));
		addRoles(authorities, jwt.getClaim("roles"));
		addPermissions(authorities, jwt.getClaim("permissions"));
		return authorities;
	}

	private static void addRoles(Set<GrantedAuthority> authorities, Object claim) {
		if (claim instanceof List<?> roles) {
			for (Object role : roles) {
				if (role != null) {
					addRole(authorities, role.toString());
				}
			}
		}
	}

	private static void addRole(Set<GrantedAuthority> authorities, String role) {
		if (role == null || role.isBlank()) {
			return;
		}
		authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase(Locale.ROOT)));
	}

	private static void addPermissions(Set<GrantedAuthority> authorities, Object claim) {
		if (!(claim instanceof List<?> permissions)) {
			return;
		}
		for (Object permission : permissions) {
			if (permission != null && !permission.toString().isBlank()) {
				authorities.add(new SimpleGrantedAuthority(permission.toString()));
			}
		}
	}

}
