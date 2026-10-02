package cl.overrid3.boilerplate.identity.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
class UserEntity {

	@Id
	private UUID id;

	@Column(name = "external_subject", nullable = false, unique = true, length = 255)
	private String externalSubject;

	@Column(length = 320)
	private String email;

	@Column(name = "display_name", length = 255)
	private String displayName;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected UserEntity() {
	}

	static UserEntity create(String externalSubject, String email, String displayName) {
		UserEntity user = new UserEntity();
		user.id = UUID.randomUUID();
		user.externalSubject = externalSubject;
		user.email = email;
		user.displayName = displayName;
		user.createdAt = Instant.now();
		return user;
	}

	UUID getId() {
		return id;
	}

	String getEmail() {
		return email;
	}

	void setEmail(String email) {
		this.email = email;
	}

	String getDisplayName() {
		return displayName;
	}

	void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
