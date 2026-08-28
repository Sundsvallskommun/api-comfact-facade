package se.sundsvall.comfactfacade.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MICROS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "account",
	uniqueConstraints = @UniqueConstraint(name = "uq_account_municipality_id_account_key", columnNames = {
		"municipality_id", "account_key"
	}),
	indexes = @Index(name = "idx_account_municipality_id", columnList = "municipality_id"))
public class AccountEntity {

	@Id
	@UuidGenerator
	@Column(name = "id", length = 255)
	private String id;

	@Column(name = "municipality_id", nullable = false, length = 255)
	private String municipalityId;

	@Column(name = "account_key", nullable = false, length = 255)
	private String accountKey;

	@Column(name = "comfact_account_id", length = 255)
	private String comfactAccountId;

	@Column(name = "client_id", nullable = false, length = 255)
	private String clientId;

	// Encrypted with EncryptionUtility (Base64 of nonce + ChaCha20-Poly1305 cipher text) - never stored in clear text.
	@Column(name = "client_secret", nullable = false, length = 512)
	private String clientSecret;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "default_account", nullable = false)
	private boolean defaultAccount;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime modified;

	public static AccountEntity create() {
		return new AccountEntity();
	}

	@PrePersist
	void onCreate() {
		created = now().truncatedTo(MICROS);
	}

	@PreUpdate
	void onUpdate() {
		modified = now().truncatedTo(MICROS);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public AccountEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public AccountEntity withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(final String accountKey) {
		this.accountKey = accountKey;
	}

	public AccountEntity withAccountKey(final String accountKey) {
		this.accountKey = accountKey;
		return this;
	}

	public String getComfactAccountId() {
		return comfactAccountId;
	}

	public void setComfactAccountId(final String comfactAccountId) {
		this.comfactAccountId = comfactAccountId;
	}

	public AccountEntity withComfactAccountId(final String comfactAccountId) {
		this.comfactAccountId = comfactAccountId;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(final String clientId) {
		this.clientId = clientId;
	}

	public AccountEntity withClientId(final String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(final String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public AccountEntity withClientSecret(final String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public AccountEntity withDescription(final String description) {
		this.description = description;
		return this;
	}

	public boolean isDefaultAccount() {
		return defaultAccount;
	}

	public void setDefaultAccount(final boolean defaultAccount) {
		this.defaultAccount = defaultAccount;
	}

	public AccountEntity withDefaultAccount(final boolean defaultAccount) {
		this.defaultAccount = defaultAccount;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public AccountEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public AccountEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, municipalityId, accountKey, comfactAccountId, clientId, clientSecret, description, defaultAccount, created, modified);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final AccountEntity other)) {
			return false;
		}
		return Objects.equals(id, other.id)
			&& Objects.equals(municipalityId, other.municipalityId)
			&& Objects.equals(accountKey, other.accountKey)
			&& Objects.equals(comfactAccountId, other.comfactAccountId)
			&& Objects.equals(clientId, other.clientId)
			&& Objects.equals(clientSecret, other.clientSecret)
			&& Objects.equals(description, other.description)
			&& defaultAccount == other.defaultAccount
			&& Objects.equals(created, other.created)
			&& Objects.equals(modified, other.modified);
	}

	@Override
	public String toString() {
		return "AccountEntity{" +
			"id='" + id + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", accountKey='" + accountKey + '\'' +
			", comfactAccountId='" + comfactAccountId + '\'' +
			", clientId='" + clientId + '\'' +
			", clientSecret='" + clientSecret + '\'' +
			", description='" + description + '\'' +
			", defaultAccount=" + defaultAccount +
			", created=" + created +
			", modified=" + modified +
			'}';
	}
}
