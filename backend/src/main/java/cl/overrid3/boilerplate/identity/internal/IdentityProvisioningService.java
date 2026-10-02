package cl.overrid3.boilerplate.identity.internal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import cl.overrid3.boilerplate.identity.CurrentPrincipal;

/**
 * Crea o actualiza usuario, organización y membresía a partir de los claims del JWT.
 */
@Service
class IdentityProvisioningService {

	private final UserRepository users;

	private final OrganizationRepository organizations;

	private final MembershipRepository memberships;

	private final TransactionTemplate transactions;

	IdentityProvisioningService(UserRepository users, OrganizationRepository organizations,
			MembershipRepository memberships, PlatformTransactionManager transactionManager) {
		this.users = users;
		this.organizations = organizations;
		this.memberships = memberships;
		this.transactions = new TransactionTemplate(transactionManager);
	}

	CurrentPrincipal provision(Jwt jwt) {
		try {
			return doProvision(jwt);
		}
		catch (DataIntegrityViolationException ex) {
			return doProvision(jwt);
		}
	}

	private CurrentPrincipal doProvision(Jwt jwt) {
		return transactions.execute(status -> {
			String externalSubject = jwt.getSubject();
			String externalOrganizationId = jwt.getClaimAsString("org_id");
			String role = roleOf(jwt);
			String email = blankToNull(jwt.getClaimAsString("email"));
			String displayName = blankToNull(jwt.getClaimAsString("name"));

			UserEntity user = users.findByExternalSubject(externalSubject).orElseGet(
					() -> users.save(UserEntity.create(externalSubject, email, displayName)));
			if (email != null && user.getEmail() == null) {
				user.setEmail(email);
			}
			if (displayName != null && user.getDisplayName() == null) {
				user.setDisplayName(displayName);
			}

			OrganizationEntity organization = organizations.findByExternalId(externalOrganizationId)
					.orElseGet(() -> organizations.save(OrganizationEntity.create(externalOrganizationId)));

			memberships.findByUserIdAndOrganizationId(user.getId(), organization.getId()).ifPresentOrElse(
					membership -> membership.setRole(role),
					() -> memberships.save(MembershipEntity.create(user.getId(), organization.getId(), role)));

			return new CurrentPrincipal(user.getId(), organization.getId(), externalSubject, externalOrganizationId, role);
		});
	}

	private static String roleOf(Jwt jwt) {
		String role = jwt.getClaimAsString("role");
		if (role == null || role.isBlank()) {
			return "member";
		}
		return role.trim();
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value;
	}

}
