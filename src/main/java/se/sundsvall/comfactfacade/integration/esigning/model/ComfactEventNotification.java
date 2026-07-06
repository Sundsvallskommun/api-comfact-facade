package se.sundsvall.comfactfacade.integration.esigning.model;

import java.time.OffsetDateTime;
import se.sundsvall.comfactfacade.api.model.Document;

/**
 * A Comfact signing event, flattened from Comfact's {@code EventNotification} and forwarded to
 * api-service-e-signing (which normalizes it into its own provider-neutral event).
 *
 * @param eventTrigger      the Comfact event trigger (e.g. {@code signingInstanceCompleted})
 * @param signingInstanceId the Comfact signing instance id
 * @param customerReference the customer reference (Postportalen MessageId) echoed back by Comfact
 * @param statusCode        the Comfact status code of the signing instance
 * @param expires           when the signing instance expires
 * @param signatory         the acting signatory, present on signatory events
 * @param signedDocument    the signed document, present on a completed event
 * @param timestamp         when the event occurred
 */
public record ComfactEventNotification(
	String eventTrigger,
	String signingInstanceId,
	String customerReference,
	String statusCode,
	OffsetDateTime expires,
	ComfactSignatory signatory,
	Document signedDocument,
	OffsetDateTime timestamp) {
}
