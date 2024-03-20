package se.sundsvall.comfactfacade.api.model;

import java.util.List;
import java.util.Objects;

import se.sundsvall.dept44.common.validators.annotation.ValidMobileNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidPersonalNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


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

	@ValidPersonalNumber(nullable = true)
	@Schema(description = "The party personal number", example = "197001011234")
	private String personalNumber;

	@Schema(description = "The party email", example = "john.doe@sundsvall.se")
	private String email;

	@ValidMobileNumber(nullable = true)
	@Schema(description = "The party phone number", example = "0701234567")
	private String phoneNumber;

	@Schema(description = "The organization of the party.", example = "Sundsvall Municipality")
	private String organization;

	@Schema(description = "Language parameter that overwrites the language of the Signing Instance for the current party.", example = "sv")
	private String language;

	@ArraySchema(schema = @Schema(implementation = Identification.class, description = "The means of identification to use to identify the signatory."))
	private List<Identification> identifications;

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Party party = (Party) o;
		return Objects.equals(name, party.name) && Objects.equals(partyId, party.partyId) && Objects.equals(notificationMessage, party.notificationMessage) && Objects.equals(title, party.title) && Objects.equals(personalNumber, party.personalNumber) && Objects.equals(email, party.email) && Objects.equals(phoneNumber, party.phoneNumber) && Objects.equals(organization, party.organization) && Objects.equals(language, party.language) && Objects.equals(identifications, party.identifications);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, partyId, notificationMessage, title, personalNumber, email, phoneNumber, organization, language, identifications);
	}

	@Override
	public String toString() {
		return "Party{" +
			"name='" + name + '\'' +
			", partyId='" + partyId + '\'' +
			", notificationMessage=" + notificationMessage +
			", title='" + title + '\'' +
			", personalNumber='" + personalNumber + '\'' +
			", email='" + email + '\'' +
			", phoneNumber='" + phoneNumber + '\'' +
			", organization='" + organization + '\'' +
			", language='" + language + '\'' +
			", identifications=" + identifications +
			'}';
	}

}
