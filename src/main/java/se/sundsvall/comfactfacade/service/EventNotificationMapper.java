package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.EventNotification;
import generated.se.sundsvall.comfact.SignatoryAction;
import generated.se.sundsvall.comfact.SignatoryInfo;
import generated.se.sundsvall.comfact.Status;
import java.util.Base64;
import java.util.Optional;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.integration.esigning.model.ComfactEventNotification;
import se.sundsvall.comfactfacade.integration.esigning.model.ComfactSignatory;

/**
 * Flattens Comfact's generated {@link EventNotification} into the {@link ComfactEventNotification} forwarded to
 * api-service-e-signing.
 */
public final class EventNotificationMapper {

	private EventNotificationMapper() {}

	public static ComfactEventNotification toEsigningEvent(final EventNotification notification) {
		final var info = notification.getSigningInstanceInfo();

		return new ComfactEventNotification(
			notification.getEventTrigger().getValue(),
			info.getSigningInstanceId(),
			info.getCustomerReferenceNumber(),
			Optional.ofNullable(info.getStatus()).map(Status::getValue).orElse(null),
			info.getExpires(),
			toSignatory(notification.getSignatoryInfo()),
			toDocument(notification.getSignedDocument()),
			notification.getTimestamp());
	}

	static ComfactSignatory toSignatory(final SignatoryInfo signatoryInfo) {
		return Optional.ofNullable(signatoryInfo)
			.map(s -> new ComfactSignatory(
				s.getPartyId(),
				Optional.ofNullable(s.getSignatoryAction()).map(action -> action.getAction().getValue()).orElse(null),
				Optional.ofNullable(s.getSignatoryAction()).map(SignatoryAction::getReason).orElse(null)))
			.orElse(null);
	}

	static Document toDocument(final generated.se.sundsvall.comfact.Document document) {
		return Optional.ofNullable(document)
			.map(d -> Document.builder()
				.withName(d.getDocumentName())
				.withFileName(d.getFileName())
				.withMimeType(d.getMimeType())
				.withContent(Optional.ofNullable(d.getContent()).map(Base64.getEncoder()::encodeToString).orElse(null))
				.build())
			.orElse(null);
	}
}
