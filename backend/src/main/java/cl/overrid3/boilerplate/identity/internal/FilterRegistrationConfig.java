package cl.overrid3.boilerplate.identity.internal;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * El filtro se agrega solo dentro de la cadena de seguridad, no como filtro de servlet suelto.
 */
@Configuration
class FilterRegistrationConfig {

	@Bean
	FilterRegistrationBean<IdentityProvisioningFilter> identityProvisioningFilterRegistration(
			IdentityProvisioningFilter filter) {
		FilterRegistrationBean<IdentityProvisioningFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

}
