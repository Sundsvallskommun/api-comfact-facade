package se.sundsvall.comfactfacade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;

import comfact.DocumentType;
import comfact.GetSignatoryRequest;
import comfact.SigningInstance;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

	@Mock
	private ComfactIntegration comfactClient;

	@InjectMocks
	private SigningService signingService;

	@Test
	void createSigningRequest() {
		// Arrange
		when(comfactClient.createSigningInstance(any())).thenReturn(new comfact.CreateSigningInstanceResponse().withSigningInstanceId("123"));

		// Act
		final var result = signingService.createSigningRequest(new SigningRequest());

		// Assert
		assertThat(result).isNotNull().isEqualTo("123");
	}

	@Test
	void updateSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var signingRequest = new SigningRequest();

		// Act
		signingService.updateSigningRequest(signingId, signingRequest);

		// Assert
		verify(comfactClient).updateSigningInstance(any());
	}


	@Test
	void cancelSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		// Act
		signingService.cancelSigningRequest(signingId);
		// Assert
		verify(comfactClient).withdrawSigningInstance(any());
	}


	@Test
	void getSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var response = new comfact.GetSigningInstanceResponse()
			.withSigningInstance(new SigningInstance()
				.withSigningInstanceId(signingId)
				.withDocument(new DocumentType()
					.withContent("someContent".getBytes(StandardCharsets.UTF_8)))
				.withStatus(new comfact.Status()
					.withStatusCode("OK")));
		when(comfactClient.getSigningInstance(any())).thenReturn(response);

		// Act
		final var result = signingService.getSigningRequest(signingId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo(signingId);
	}


	@Test
	void getSigningRequests() {
		// Arrange
		final var response = new comfact.GetSigningInstanceInfoResponse()
			.withSigningInstanceInfos(
				new comfact.SigningInstanceInfo().withSigningInstanceId("123").withStatus(new comfact.Status().withStatusCode("OK")),
				new comfact.SigningInstanceInfo().withSigningInstanceId("456").withStatus(new comfact.Status().withStatusCode("OK")));

		when(comfactClient.getSigningInstanceInfo(any())).thenReturn(response);

		// Act
		final var result = signingService.getSigningRequests();

		// Assert
		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.getFirst().getSigningId()).isEqualTo("123");
		assertThat(result.getLast().getSigningId()).isEqualTo("456");
		verify(comfactClient).getSigningInstanceInfo(any());
	}

	@Test
	void getSignatory() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		when(comfactClient.getSignatory(any())).thenReturn(new comfact.GetSignatoryResponse()
			.withSignatories(new comfact.Signatory()
				.withPartyId(partyId)));

		// Act
		final var result = signingService.getSignatory(signingId, partyId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getPartyId()).isEqualTo(partyId);
		verify(comfactClient).getSignatory(any(GetSignatoryRequest.class));
	}

}
