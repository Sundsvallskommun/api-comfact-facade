package se.sundsvall.comfactfacade.api.model;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The signing request.")
public record SigningRequest(

	@Schema(description = "The customer reference", example = "1234567890")
	String customerReference,

	@Schema(description = "The date and time when the signing request expires.", example = "2021-12-31T23:59:59Z")
	OffsetDateTime expires,

	@Schema(description = "Custom message for the signature request emails.")
	NotificationMessage notificationMessage,

	@Valid
	@NotNull
	@Schema(description = "The initiator of the signing request.")
	Party initiator,

	@Valid
	@Schema(description = "A party that is not part of the signing process but will get a copy of the signed document when the signing instance is completed.")
	Party additionalParty,

	@Valid
	@NotNull
	@Schema(description = "The party that will sign the documents.")
	Party signatory,

	@Valid
	@NotNull
	@Schema(description = "Information about the main document to sign.")
	Document document,

	@ArraySchema(schema = @Schema(implementation = Document.class, description = "Additional documents to sign."))
	List<Document> additionalDocuments,

	@Schema(description = "The language of the signing process.", example = "sv")
	String language) {

}
