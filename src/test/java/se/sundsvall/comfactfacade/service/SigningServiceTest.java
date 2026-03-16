package se.sundsvall.comfactfacade.service;

import comfact.CreateSigningInstanceRequest;
import comfact.DocumentType;
import comfact.GetSignatoryRequest;
import comfact.GetSigningInstanceInfoRequest;
import comfact.GetSigningInstanceRequest;
import comfact.SigningInstance;
import comfact.UpdateSigningInstanceRequest;
import comfact.WithdrawSigningInstanceRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;
import se.sundsvall.comfactfacade.integration.party.PartyClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

	@Mock
	private PartyClient partyClientMock;

	@Mock
	private Pageable pageableMock;

	@Mock
	private ComfactIntegration comfactIntegrationMock;

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
		final var partyId = "partyId";
		final var request = SigningRequest.builder()
			.withInitiator(Party.builder()
				.withPartyId(partyId)
				.build())
			.withSignatories(List.of(Signatory.builder()
				.withPartyId(partyId)
				.build()))
			.build();

		when(partyClientMock.getLegalId(MUNICIPALITY_ID, partyId, "PRIVATE")).thenReturn("someLegalId");
		when(comfactIntegrationMock.createSigningInstance(anyString(), any(CreateSigningInstanceRequest.class))).thenReturn(new comfact.CreateSigningInstanceResponse()
			.withSigningInstanceId("123")
			.withSignatoryUrls(new comfact.SignatoryUrl().withPartyId(partyId).withValue("someUrl")));

		// Act
		final var result = signingService.createSigningRequest(MUNICIPALITY_ID, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo("123");
		assertThat(result.getSignatoryUrls()).hasSize(1);
		assertThat(result.getSignatoryUrls()).containsEntry("partyId", "someUrl");
		verify(comfactIntegrationMock).createSigningInstance(eq(MUNICIPALITY_ID), createRequestCaptor.capture());
		assertThat(createRequestCaptor.getValue()).isNotNull();
		assertThat(createRequestCaptor.getValue().getSigningInstanceInput()).isNotNull();
		assertThat(createRequestCaptor.getValue().getSigningInstanceInput().getSignatories()).hasSize(1).satisfies(
			signatory -> assertThat(signatory.getFirst().getPartyId()).isEqualTo(partyId));
	}

	@Test
	void updateSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "partyId";
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder()
				.withPartyId(partyId)
				.build()))
			.build();

		when(partyClientMock.getLegalId(MUNICIPALITY_ID, partyId, "PRIVATE")).thenReturn("someLegalId");

		// Act
		signingService.updateSigningRequest(MUNICIPALITY_ID, signingId, signingRequest);

		// Assert
		verify(comfactIntegrationMock).updateSigningInstance(eq(MUNICIPALITY_ID), updateRequestCaptor.capture());
		assertThat(updateRequestCaptor.getValue()).isNotNull();
		assertThat(updateRequestCaptor.getValue().getSigningInstanceId()).isEqualTo(signingId);
		assertThat(updateRequestCaptor.getValue().getSigningInstanceInput()).isNotNull();
	}

	@Test
	void cancelSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		// Act
		signingService.cancelSigningRequest(MUNICIPALITY_ID, signingId);
		// Assert
		verify(comfactIntegrationMock).withdrawSigningInstance(eq(MUNICIPALITY_ID), withdrawRequestCaptor.capture());
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
		when(comfactIntegrationMock.getSigningInstance(anyString(), any(GetSigningInstanceRequest.class))).thenReturn(response);

		// Act
		final var result = signingService.getSigningRequest(MUNICIPALITY_ID, signingId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo(signingId);
		verify(comfactIntegrationMock).getSigningInstance(eq(MUNICIPALITY_ID), getSigningInstanceRequestCaptor.capture());
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

		when(comfactIntegrationMock.getSigningInstanceInfo(anyString(), any())).thenReturn(response);
		when(pageableMock.getPageNumber()).thenReturn(0);
		when(pageableMock.getPageSize()).thenReturn(2);
		when(pageableMock.getSort()).thenReturn(Sort.by(Sort.Order.asc("Created")));
		// Act
		final var result = signingService.getSigningRequests(MUNICIPALITY_ID, pageableMock);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningInstances()).hasSize(2);
		assertThat(result.getSigningInstances().getFirst().getSigningId()).isEqualTo("123");
		assertThat(result.getSigningInstances().getLast().getSigningId()).isEqualTo("456");

		verify(comfactIntegrationMock).getSigningInstanceInfo(eq(MUNICIPALITY_ID), getSigningInstanceInfoRequestCaptor.capture());
		assertThat(getSigningInstanceInfoRequestCaptor.getValue()).isNotNull();
		assertThat(getSigningInstanceInfoRequestCaptor.getValue().getCustom().getAnies()).hasSize(1);
	}

	@Test
	void getSignatory() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		when(comfactIntegrationMock.getSignatory(anyString(), any(GetSignatoryRequest.class))).thenReturn(new comfact.GetSignatoryResponse()
			.withSignatories(new comfact.Signatory()
				.withPartyId(partyId)));

		// Act
		final var result = signingService.getSignatory(MUNICIPALITY_ID, signingId, partyId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getPartyId()).isEqualTo(partyId);
		verify(comfactIntegrationMock).getSignatory(eq(MUNICIPALITY_ID), getSignatoryRequestCaptor.capture());
		assertThat(getSignatoryRequestCaptor.getValue()).isNotNull();
		assertThat(getSignatoryRequestCaptor.getValue().getSigningInstanceId()).isEqualTo(signingId);
		assertThat(getSignatoryRequestCaptor.getValue().getPartyId()).isEqualTo(partyId);
	}

}
