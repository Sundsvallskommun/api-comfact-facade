package se.sundsvall.comfactfacade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;

import comfact.CreateSigningInstanceRequest;
import comfact.DocumentType;
import comfact.GetSignatoryRequest;
import comfact.GetSigningInstanceInfoRequest;
import comfact.GetSigningInstanceRequest;
import comfact.SigningInstance;
import comfact.UpdateSigningInstanceRequest;
import comfact.WithdrawSigningInstanceRequest;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

	@Mock
	private ComfactIntegration comfactIntegration;

	@InjectMocks
	private SigningService signingService;

	@Captor
	private ArgumentCaptor<CreateSigningInstanceRequest> createRequestCaptor;

	@Captor
	private ArgumentCaptor<UpdateSigningInstanceRequest> updateRequestCaptor;

	@Captor
	private ArgumentCaptor<WithdrawSigningInstanceRequest> withdrawRequestCaptor;

	@Captor
	private ArgumentCaptor<GetSigningInstanceRequest> getSigningInstanceRequestCaptor;

	@Captor
	private ArgumentCaptor<GetSigningInstanceInfoRequest> getSigningInstanceInfoRequestCaptor;

	@Captor
	private ArgumentCaptor<GetSignatoryRequest> getSignatoryRequestCaptor;


	@Test
	void createSigningRequest() {
		// Arrange
		when(comfactIntegration.createSigningInstance(any(CreateSigningInstanceRequest.class))).thenReturn(new comfact.CreateSigningInstanceResponse().withSigningInstanceId("123"));

		// Act
		final var result = signingService.createSigningRequest(new SigningRequest());

		// Assert
		assertThat(result).isNotNull().isEqualTo("123");
		verify(comfactIntegration).createSigningInstance(createRequestCaptor.capture());
		assertThat(createRequestCaptor.getValue()).isNotNull();
	}

	@Test
	void updateSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var signingRequest = new SigningRequest();

		// Act
		signingService.updateSigningRequest(signingId, signingRequest);

		// Assert
		verify(comfactIntegration).updateSigningInstance(updateRequestCaptor.capture());
		assertThat(updateRequestCaptor.getValue()).isNotNull();
		assertThat(updateRequestCaptor.getValue().getSigningInstanceId()).isEqualTo(signingId);
		assertThat(updateRequestCaptor.getValue().getSigningInstanceInput()).isNotNull();
	}


	@Test
	void cancelSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		// Act
		signingService.cancelSigningRequest(signingId);
		// Assert
		verify(comfactIntegration).withdrawSigningInstance(withdrawRequestCaptor.capture());
		assertThat(withdrawRequestCaptor.getValue()).isNotNull();
		assertThat(withdrawRequestCaptor.getValue().getSigningInstanceId()).isEqualTo(signingId);
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
		when(comfactIntegration.getSigningInstance(any(GetSigningInstanceRequest.class))).thenReturn(response);

		// Act
		final var result = signingService.getSigningRequest(signingId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo(signingId);
		verify(comfactIntegration).getSigningInstance(getSigningInstanceRequestCaptor.capture());
		assertThat(getSigningInstanceRequestCaptor.getValue()).isNotNull();
		assertThat(getSigningInstanceRequestCaptor.getValue().getSigningInstanceId()).isEqualTo(signingId);
	}


	@Test
	void getSigningRequests() {
		// Arrange
		final var response = new comfact.GetSigningInstanceInfoResponse()
			.withSigningInstanceInfos(
				new comfact.SigningInstanceInfo().withSigningInstanceId("123").withStatus(new comfact.Status().withStatusCode("OK")),
				new comfact.SigningInstanceInfo().withSigningInstanceId("456").withStatus(new comfact.Status().withStatusCode("OK")));

		when(comfactIntegration.getSigningInstanceInfo(any(GetSigningInstanceInfoRequest.class))).thenReturn(response);

		// Act
		final var result = signingService.getSigningRequests();

		// Assert
		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.getFirst().getSigningId()).isEqualTo("123");
		assertThat(result.getLast().getSigningId()).isEqualTo("456");
		verify(comfactIntegration).getSigningInstanceInfo(getSigningInstanceInfoRequestCaptor.capture());
		assertThat(getSigningInstanceInfoRequestCaptor.getValue()).isNotNull();
	}

	@Test
	void getSignatory() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		when(comfactIntegration.getSignatory(any(GetSignatoryRequest.class))).thenReturn(new comfact.GetSignatoryResponse()
			.withSignatories(new comfact.Signatory()
				.withPartyId(partyId)));

		// Act
		final var result = signingService.getSignatory(signingId, partyId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getPartyId()).isEqualTo(partyId);
		verify(comfactIntegration).getSignatory(getSignatoryRequestCaptor.capture());
		assertThat(getSignatoryRequestCaptor.getValue()).isNotNull();
		assertThat(getSignatoryRequestCaptor.getValue().getSigningInstanceId()).isEqualTo(signingId);
		assertThat(getSignatoryRequestCaptor.getValue().getPartyId()).isEqualTo(partyId);
	}

}
