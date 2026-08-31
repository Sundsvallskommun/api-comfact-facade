package se.sundsvall.comfactfacade.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "A Comfact account configuration mapping.")
public class Account {

	@Schema(description = "The unique id of the account mapping", examples = "5a1efd51-1b1e-4a20-9673-6b6f79f27f01", accessMode = READ_ONLY)
	private String id;

	@NotBlank
	@Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "must only contain letters, digits, '.', '_' and '-'")
	@Schema(description = "The key used to select this account via the x-account-key header", examples = "social-services")
	private String accountKey;

	@Schema(description = "Optional id of the account configuration in Comfact Signature, sent as accountId when creating signing instances", examples = "1234567890")
	private String comfactAccountId;

	@NotBlank
	@Schema(description = "The OAuth2 client id used to authenticate against Comfact for this account", examples = "some-client-id")
	private String clientId;

	@NotBlank
	@JsonProperty(access = WRITE_ONLY)
	@Schema(description = "The OAuth2 client secret used to authenticate against Comfact for this account. Stored encrypted and never returned in responses.", examples = "some-client-secret", accessMode = Schema.AccessMode.WRITE_ONLY)
	private String clientSecret;

	@Schema(description = "Optional description of the account", examples = "Social services e-signing account")
	private String description;

	@Schema(description = "Whether this account is the municipality's default, used when no x-account-key header is sent. Setting this to true clears the flag on any previous default.", examples = "false")
	private Boolean defaultAccount;

	@Schema(description = "When the account mapping was created", examples = "2026-08-28T12:00:00+02:00", accessMode = READ_ONLY)
	private OffsetDateTime created;

	@Schema(description = "When the account mapping was last modified", examples = "2026-08-28T12:00:00+02:00", accessMode = READ_ONLY)
	private OffsetDateTime modified;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Account account = (Account) o;
		return Objects.equals(id, account.id) && Objects.equals(accountKey, account.accountKey) && Objects.equals(comfactAccountId, account.comfactAccountId)
			&& Objects.equals(clientId, account.clientId) && Objects.equals(clientSecret, account.clientSecret) && Objects.equals(description, account.description)
			&& Objects.equals(defaultAccount, account.defaultAccount) && Objects.equals(created, account.created) && Objects.equals(modified, account.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, accountKey, comfactAccountId, clientId, clientSecret, description, defaultAccount, created, modified);
	}

	@Override
	public String toString() {
		return "Account{" +
			"id='" + id + '\'' +
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
