package se.sundsvall.comfactfacade.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import se.sundsvall.dept44.common.validators.annotation.ValidMobileNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(setterPrefix = "with")
@Schema(description = "A party related to the signing process.")
public class Party {

	@Schema(description = "The name of the party", example = "John Doe")
	private String name;

	@ValidUuid(nullable = true)
	@Schema(description = "The party id", example = "550e8400-e29b-41d4-a716-446655440000")
	private String partyId;

	@Schema(description = "Custom message for the signature request emails for the specific party. Overwrites the message in the Signing Request.")
	private NotificationMessage notificationMessage;

	@Schema(description = "The party title", example = "CEO")
	private String title;

	@NotNull
	@Schema(description = "The party email", example = "john.doe@sundsvall.se")
	private String email;

	@ValidMobileNumber(nullable = true)
	@Schema(description = "The party phone number", example = "0701740605")
	private String phoneNumber;

	@Schema(description = "The organization of the party.", example = "Sundsvall Municipality")
	private String organization;

	@Schema(description = "Language parameter that overwrites the language of the Signing Instance for the current party.", example = "sv")
	private String language;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Party party = (Party) o;
		return Objects.equals(name, party.name) && Objects.equals(partyId, party.partyId) && Objects.equals(notificationMessage, party.notificationMessage) && Objects.equals(title, party.title) && Objects.equals(email, party.email) && Objects.equals(
			phoneNumber, party.phoneNumber) && Objects.equals(organization, party.organization) && Objects.equals(language, party.language);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, partyId, notificationMessage, title, email, phoneNumber, organization, language);
	}

	@Override
	public String toString() {
		return "Party{" +
			"name='" + name + '\'' +
			", partyId='" + partyId + '\'' +
			", notificationMessage=" + notificationMessage +
			", title='" + title + '\'' +
			", email='" + email + '\'' +
			", phoneNumber='" + phoneNumber + '\'' +
			", organization='" + organization + '\'' +
			", language='" + language + '\'' +
			'}';
	}

}
