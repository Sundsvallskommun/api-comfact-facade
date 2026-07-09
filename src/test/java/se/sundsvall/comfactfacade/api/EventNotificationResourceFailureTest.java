package se.sundsvall.comfactfacade.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.service.EventNotificationService;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static se.sundsvall.comfactfacade.TestUtil.createEventNotification;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class EventNotificationResourceFailureTest {

	@MockitoBean
	private EventNotificationService eventNotificationServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void handleComfactEvent_invalidMunicipalityId() {
		webTestClient.post()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/webhooks/comfact").build(Map.of("municipalityId", "invalid")))
			.contentType(APPLICATION_JSON)
			.bodyValue(createEventNotification())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON);

		verifyNoInteractions(eventNotificationServiceMock);
	}
}
