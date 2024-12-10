package se.sundsvall.comfactfacade.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Identification;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.service.SigningService;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceTest {

	@MockitoBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSigningRequests() {

		// Arrange
		when(signingServiceMock.getSigningRequests(anyString(), any()))
			.thenReturn(SigningsResponse.builder()
				.withSigningInstances(List.of(new SigningInstance(), new SigningInstance()))
				.withPagingAndSortingMetaData(PagingAndSortingMetaData.create())
				.build());

		// Act
		final var result = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/signings").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk().expectBody(SigningsResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		verify(signingServiceMock).getSigningRequests(eq(MUNICIPALITY_ID), any());
		assertThat(result).isNotNull();
		assertThat(result.getSigningInstances()).hasSize(2);
		assertThat(result.getPagingAndSortingMetaData()).isNotNull();

	}

	@Test
	void createSigningRequest() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder()
				.withIdentifications(List.of(Identification.builder().withAlias("SmsCode").build()))
				.withEmail("someEmail").build()))
			.withInitiator(Party.builder().withEmail("someEmail").build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("application/pdf")
				.withContent("someContent")
				.build())
			.build();

		when(signingServiceMock.createSigningRequest(MUNICIPALITY_ID, signingRequest))
			.thenReturn(CreateSigningResponse.builder()
				.withSigningId("someSigningId")
				.withSignatoryUrls(Map.of("somePartyId", "someUrl"))
				.build());

		// Act
		final var result = webTestClient.post()
			.uri("/{municipalityId}/signings", MUNICIPALITY_ID)
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
		verify(signingServiceMock).createSigningRequest(MUNICIPALITY_ID, signingRequest);
	}

	@Test
	void updateSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder().withIdentifications(List.of(Identification.builder().withAlias("EmailCode").build())).withEmail("someEmail").build()))
			.withInitiator(Party.builder().withEmail("someEmail").build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("application/pdf")
				.withContent("someContent")
				.build())
			.build();

		// Act
		webTestClient.patch()
			.uri("/{municipalityId}/signings/{signingId}", MUNICIPALITY_ID, signingId)
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus()
			.isNoContent();

		// Assert
		verify(signingServiceMock).updateSigningRequest(MUNICIPALITY_ID, signingId, signingRequest);
	}

	@Test
	void cancelSigningRequest() {
		// Arrange
		final String signingId = "someSigningId";

		// Act
		webTestClient.delete()
			.uri("/{municipalityId}/signings/{signingId}", MUNICIPALITY_ID, signingId)
			.exchange()
			.expectStatus()
			.isNoContent();

		// Assert
		verify(signingServiceMock).cancelSigningRequest(MUNICIPALITY_ID, signingId);
	}

	@Test
	void getSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		when(signingServiceMock.getSigningRequest(MUNICIPALITY_ID, signingId)).thenReturn(new SigningInstance());

		// Act
		final var result = webTestClient.get()
			.uri("/{municipalityId}/signings/{signingId}", MUNICIPALITY_ID, signingId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(SigningInstance.class)
			.returnResult().getResponseBody();

		// Assert
		verify(signingServiceMock).getSigningRequest(MUNICIPALITY_ID, signingId);
		assertThat(result).isNotNull();
	}

	@Test
	void getSignatory() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		when(signingServiceMock.getSignatory(MUNICIPALITY_ID, signingId, partyId)).thenReturn(new Signatory());

		// Act & Assert
		final var result = webTestClient.get()
			.uri("/{municipalityId}/signings/{signingId}/signatory/{partyId}", MUNICIPALITY_ID, signingId, partyId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(Party.class)
			.returnResult()
			.getResponseBody();

		// Assert
		verify(signingServiceMock).getSignatory(MUNICIPALITY_ID, signingId, partyId);
		assertThat(result).isNotNull();
	}

}
