package se.sundsvall.comfactfacade.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(setterPrefix = "with")
@Schema(description = "The signing instance.")
@NoArgsConstructor
@AllArgsConstructor
public class SigningInstance {

	@Schema(description = "Custom message for the signature request emails.")
	private List<NotificationMessage> notificationMessages;

	@NotNull
	@Schema(description = "The initiator of the signing request.")
	private Party initiator;

	@Schema(description = "A party that is not part of the signing process but will get a copy of the signed document when the signing instance is completed.")
	private List<Party> additionalParties;

	@NotEmpty
	@Schema(description = "The parties that will sign the documents.")
	private List<Signatory> signatories;

	@Schema(description = "Status of the signing request.")
	private Status status;

	@Schema(description = "The customer reference", examples = "1234567890")
	private String customerReference;

	@Schema(description = "The date and time when the signing instance was created.", examples = "2021-12-31T23:59:59Z")
	private OffsetDateTime created;

	@Schema(description = "The date and time when the signing instance was last changed.", examples = "2021-12-31T23:59:59Z")
	private OffsetDateTime changed;

	@Schema(description = "The date and time when the signing instance expires", examples = "2021-12-31T23:59:59Z")
	private OffsetDateTime expires;

	@Schema(description = "Information about the main document to sign.")
	private Document document;

	@Schema(description = "Information about the signed document")
	private Document signedDocument;

	@ArraySchema(schema = @Schema(implementation = Document.class, description = "Additional documents to sign."))
	private List<Document> additionalDocuments;

	@Schema(description = "The signing instance id.", examples = "1234567890")
	private String signingId;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final SigningInstance that = (SigningInstance) o;
		return Objects.equals(notificationMessages, that.notificationMessages) && Objects.equals(initiator, that.initiator) && Objects.equals(additionalParties, that.additionalParties) && Objects.equals(signatories, that.signatories) && Objects.equals(
			status, that.status) && Objects.equals(customerReference, that.customerReference) && Objects.equals(created, that.created) && Objects.equals(changed, that.changed) && Objects.equals(expires, that.expires) && Objects.equals(document,
				that.document) && Objects.equals(signedDocument, that.signedDocument) && Objects.equals(additionalDocuments, that.additionalDocuments) && Objects.equals(signingId, that.signingId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(notificationMessages, initiator, additionalParties, signatories, status, customerReference, created, changed, expires, document, signedDocument, additionalDocuments, signingId);
	}

	@Override
	public String toString() {
		return "SigningInstance{" +
			"notificationMessages=" + notificationMessages +
			", initiator=" + initiator +
			", additionalParties=" + additionalParties +
			", signatories=" + signatories +
			", status=" + status +
			", customerReference='" + customerReference + '\'' +
			", created=" + created +
			", changed=" + changed +
			", expires=" + expires +
			", document=" + document +
			", signedDocument=" + signedDocument +
			", additionalDocuments=" + additionalDocuments +
			", signingId='" + signingId + '\'' +
			'}';
	}

}
