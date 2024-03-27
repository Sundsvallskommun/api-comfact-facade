package se.sundsvall.comfactfacade.api;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.service.SigningService;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;


@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceTest {

	@MockBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSigningRequests() {

		// Arrange
		when(signingServiceMock.getSigningRequests(any()))
			.thenReturn(SigningsResponse.builder()
				.withSigningInstances(List.of(new SigningInstance(), new SigningInstance()))
				.withPagingAndSortingMetaData(PagingAndSortingMetaData.create())
				.build());

		// Act
		final var result = webTestClient.get()
			.uri("/signings")
			.exchange()
			.expectStatus().isOk().expectBody(SigningsResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		verify(signingServiceMock).getSigningRequests(any());
		assertThat(result).isNotNull();
		assertThat(result.getSigningInstances()).hasSize(2);
		assertThat(result.getPagingAndSortingMetaData()).isNotNull();

	}


	@Test
	void createSigningRequest() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder().build())
			.withInitiator(Party.builder().build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("someMimeType")
				.withContent("someContent")
				.build())
			.build();

		when(signingServiceMock.createSigningRequest(signingRequest))
			.thenReturn(CreateSigningResponse.builder()
				.withSigningId("someSigningId")
				.withSignatoryUrls(Map.of("somePartyId", "someUrl"))
				.build());

		// Act
		final var result = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(CreateSigningResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo("someSigningId");
		assertThat(result.getSignatoryUrls()).hasSize(1);
		assertThat(result.getSignatoryUrls()).containsEntry("somePartyId", "someUrl");
		verify(signingServiceMock).createSigningRequest(signingRequest);
	}

	@Test
	void updateSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder().build())
			.withInitiator(Party.builder().build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("someMimeType")
				.withContent("someContent")
				.build())
			.build();

		// Act
		webTestClient.patch()
			.uri("/signings/{signingId}", signingId)
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus()
			.isNoContent();

		// Assert
		verify(signingServiceMock).updateSigningRequest(signingId, signingRequest);
	}


	@Test
	void cancelSigningRequest() {
		// Arrange
		final String signingId = "someSigningId";

		// Act
		webTestClient.delete()
			.uri("/signings/{signingId}", signingId)
			.exchange()
			.expectStatus()
			.isNoContent();

		// Assert
		verify(signingServiceMock).cancelSigningRequest(signingId);
	}


	@Test
	void getSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		when(signingServiceMock.getSigningRequest(signingId)).thenReturn(new SigningInstance());

		// Act
		final var result = webTestClient.get()
			.uri("/signings/{signingId}", signingId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(SigningInstance.class)
			.returnResult().getResponseBody();

		// Assert
		verify(signingServiceMock).getSigningRequest(signingId);
		assertThat(result).isNotNull();
	}


	@Test
	void getSignatory() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		when(signingServiceMock.getSignatory(signingId, partyId)).thenReturn(new Party());

		// Act & Assert
		final var result = webTestClient.get()
			.uri("/signings/{signingId}/signatory/{partyId}", signingId, partyId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(Party.class)
			.returnResult()
			.getResponseBody();


		// Assert
		verify(signingServiceMock).getSignatory(signingId, partyId);
		assertThat(result).isNotNull();
	}

}
