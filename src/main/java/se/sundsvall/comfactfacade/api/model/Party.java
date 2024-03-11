package se.sundsvall.comfactfacade.api.model;

import se.sundsvall.dept44.common.validators.annotation.ValidMobileNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidPersonalNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A party related to the signing process.")
public record Party(
	@Schema(description = "The name of the party", example = "John Doe")
	String name,

	@ValidUuid(nullable = true)
	@Schema(description = "The party id", example = "550e8400-e29b-41d4-a716-446655440000")
	String partyId,

	@Schema(description = "The party title", example = "CEO")
	String title,

	@ValidPersonalNumber(nullable = true)
	@Schema(description = "The party personal number", example = "197001011234")
	String personalNumber,

	@Schema(description = "The party email", example = "john.doe@sundsvall.se")
	String email,

	@ValidMobileNumber(nullable = true)
	@Schema(description = "The party phone number", example = "0701234567")
	String phoneNumber,

	@Schema(description = "The organization of the party.", example = "Sundsvall Municipality")
	String organization,

	@Schema(description = "Language parameter that overwrites the language of the Signing Instance for the current party.", example = "sv")
	String language,

	@Schema(description = "The means of identification to use to identify the signatory.")
	Identification identification
) {

}
