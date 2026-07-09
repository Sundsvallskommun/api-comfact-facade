package se.sundsvall.comfactfacade;

import generated.se.sundsvall.comfact.Action;
import generated.se.sundsvall.comfact.Document;
import generated.se.sundsvall.comfact.EventNotification;
import generated.se.sundsvall.comfact.EventTrigger;
import generated.se.sundsvall.comfact.SignatoryAction;
import generated.se.sundsvall.comfact.SignatoryInfo;
import generated.se.sundsvall.comfact.SigningInstanceInfo;
import generated.se.sundsvall.comfact.Status;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

public final class TestUtil {

	private TestUtil() {}

	public static EventNotification createEventNotification() {
		return new EventNotification()
			.eventTrigger(EventTrigger.SIGNING_INSTANCE_COMPLETED)
			.signingInstanceInfo(new SigningInstanceInfo()
				.signingInstanceId("comfact-123")
				.customerReferenceNumber("550e8400-e29b-41d4-a716-446655440000")
				.status(Status.COMPLETED)
				.expires(OffsetDateTime.now().plusDays(30)))
			.signatoryInfo(new SignatoryInfo()
				.partyId("party-1")
				.signatoryAction(new SignatoryAction()
					.action(Action.APPROVED)
					.reason("reason")
					.timestamp(OffsetDateTime.now())))
			.signedDocument(new Document()
				.documentName("Contract")
				.fileName("signed.pdf")
				.mimeType("application/pdf")
				.content("test".getBytes(StandardCharsets.UTF_8)))
			.timestamp(OffsetDateTime.now());
	}
}
