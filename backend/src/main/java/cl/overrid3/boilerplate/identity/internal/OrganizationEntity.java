package cl.overrid3.boilerplate.identity.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "organizations")
class OrganizationEntity {

	@Id
	private UUID id;

	@Column(name = "external_id", nullable = false, unique = true, length = 255)
	private String externalId;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected OrganizationEntity() {
	}

	static OrganizationEntity create(String externalId) {
		OrganizationEntity organization = new OrganizationEntity();
		organization.id = UUID.randomUUID();
		organization.externalId = externalId;
		organization.name = externalId;
		organization.createdAt = Instant.now();
		return organization;
	}

	UUID getId() {
		return id;
	}

	String getExternalId() {
		return externalId;
	}

}
