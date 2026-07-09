package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.SignatoryInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.comfactfacade.TestUtil.createEventNotification;

class EventNotificationMapperTest {

	@Test
	void toEsigningEvent() {
		final var notification = createEventNotification();

		final var event = EventNotificationMapper.toEsigningEvent(notification);

		assertThat(event.eventTrigger()).isEqualTo("signingInstanceCompleted");
		assertThat(event.signingInstanceId()).isEqualTo("comfact-123");
		assertThat(event.customerReference()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
		assertThat(event.statusCode()).isEqualTo("completed");
		assertThat(event.expires()).isEqualTo(notification.getSigningInstanceInfo().getExpires());
		assertThat(event.timestamp()).isEqualTo(notification.getTimestamp());

		assertThat(event.signatory()).isNotNull();
		assertThat(event.signatory().partyId()).isEqualTo("party-1");
		assertThat(event.signatory().action()).isEqualTo("approved");
		assertThat(event.signatory().reason()).isEqualTo("reason");

		assertThat(event.signedDocument()).isNotNull();
		assertThat(event.signedDocument().getName()).isEqualTo("Contract");
		assertThat(event.signedDocument().getFileName()).isEqualTo("signed.pdf");
		assertThat(event.signedDocument().getMimeType()).isEqualTo("application/pdf");
		assertThat(event.signedDocument().getContent()).isEqualTo("dGVzdA==");
	}

	@Test
	void toEsigningEvent_noSignatoryOrDocument() {
		final var notification = createEventNotification().signatoryInfo(null).signedDocument(null);

		final var event = EventNotificationMapper.toEsigningEvent(notification);

		assertThat(event.signatory()).isNull();
		assertThat(event.signedDocument()).isNull();
	}

	@Test
	void toEsigningEvent_signatoryWithoutAction() {
		final var notification = createEventNotification().signatoryInfo(new SignatoryInfo().partyId("party-9"));

		final var event = EventNotificationMapper.toEsigningEvent(notification);

		assertThat(event.signatory().partyId()).isEqualTo("party-9");
		assertThat(event.signatory().action()).isNull();
		assertThat(event.signatory().reason()).isNull();
	}
}
