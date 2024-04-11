package se.sundsvall.comfactfacade.api.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "The signing request.")
public class SigningRequest {

	@Valid
	@ArraySchema(schema = @Schema(implementation = Document.class, description = "Additional documents to sign."))
	private List<Document> additionalDocuments;

	@Schema(description = "The language of the signing process.", example = "sv-SE")
	private String language;

	@Schema(description = "The customer reference", example = "1234567890")
	private String customerReference;

	@Schema(description = "The date and time when the signing request expires.", example = "2021-12-31T23:59:59Z")
	private OffsetDateTime expires;

	@Valid
	@Schema(description = "Custom message for the signature request emails.")
	private NotificationMessage notificationMessage;

	@Valid
	@Schema(description = "A reminder for a signature request.")
	private Reminder reminder;

	@Valid
	@NotNull
	@Schema(description = "The initiator of the signing request.")
	private Party initiator;

	@Valid
	@Schema(description = "A party that is not part of the signing process but will get a copy of the signed document when the signing instance is completed.")
	private Party additionalParty;

	@Valid
	@NotEmpty
	@Schema(description = "The parties that will sign the documents.")
	private List<Signatory> signatories;

	@Valid
	@NotNull
	@Schema(description = "Information about the main document to sign.")
	private Document document;

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final SigningRequest that = (SigningRequest) o;
		return Objects.equals(additionalDocuments, that.additionalDocuments) && Objects.equals(language, that.language) && Objects.equals(customerReference, that.customerReference) && Objects.equals(expires, that.expires) && Objects.equals(notificationMessage, that.notificationMessage) && Objects.equals(reminder, that.reminder) && Objects.equals(initiator, that.initiator) && Objects.equals(additionalParty, that.additionalParty) && Objects.equals(signatories, that.signatories) && Objects.equals(document, that.document);
	}

	@Override
	public int hashCode() {
		return Objects.hash(additionalDocuments, language, customerReference, expires, notificationMessage, reminder, initiator, additionalParty, signatories, document);
	}

	@Override
	public String toString() {
		return "SigningRequest{" +
			"additionalDocuments=" + additionalDocuments +
			", language='" + language + '\'' +
			", customerReference='" + customerReference + '\'' +
			", expires=" + expires +
			", notificationMessage=" + notificationMessage +
			", reminder=" + reminder +
			", initiator=" + initiator +
			", additionalParty=" + additionalParty +
			", signatories=" + signatories +
			", document=" + document +
			'}';
	}

}
