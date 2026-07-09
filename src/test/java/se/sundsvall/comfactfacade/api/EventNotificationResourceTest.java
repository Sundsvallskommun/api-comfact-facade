package se.sundsvall.comfactfacade.api;

import generated.se.sundsvall.comfact.EventNotification;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.service.EventNotificationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;
import static se.sundsvall.comfactfacade.TestUtil.createEventNotification;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class EventNotificationResourceTest {

	@MockitoBean
	private EventNotificationService eventNotificationServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void handleComfactEvent() {
		final var notification = createEventNotification();

		webTestClient.post()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/webhooks/comfact").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(notification)
			.exchange()
			.expectStatus().isOk();

		final var captor = ArgumentCaptor.forClass(EventNotification.class);
		verify(eventNotificationServiceMock).handleComfactEvent(eq(MUNICIPALITY_ID), captor.capture());
		verifyNoMoreInteractions(eventNotificationServiceMock);

		assertThat(captor.getValue().getSigningInstanceInfo().getSigningInstanceId()).isEqualTo("comfact-123");
		assertThat(captor.getValue().getEventTrigger().getValue()).isEqualTo("signingInstanceCompleted");
	}
}
