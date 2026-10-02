package cl.overrid3.boilerplate.identity.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "memberships", uniqueConstraints = @UniqueConstraint(name = "uq_memberships_user_org", columnNames = {
		"user_id", "organization_id" }))
class MembershipEntity {

	@Id
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "organization_id", nullable = false)
	private UUID organizationId;

	@Column(nullable = false, length = 100)
	private String role;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected MembershipEntity() {
	}

	static MembershipEntity create(UUID userId, UUID organizationId, String role) {
		MembershipEntity membership = new MembershipEntity();
		membership.id = UUID.randomUUID();
		membership.userId = userId;
		membership.organizationId = organizationId;
		membership.role = role;
		membership.createdAt = Instant.now();
		return membership;
	}

	String getRole() {
		return role;
	}

	void setRole(String role) {
		this.role = role;
	}

}
