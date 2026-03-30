package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.Paginator;
import generated.se.sundsvall.comfact.Property;
import generated.se.sundsvall.comfact.SearchFilter;
import generated.se.sundsvall.comfact.SearchResult;
import generated.se.sundsvall.comfact.SigningInstanceInput;
import generated.se.sundsvall.comfact.SigningInstancePatch;
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
	private ArgumentCaptor<SigningInstanceInput> inputCaptor;

	@Captor
	private ArgumentCaptor<SigningInstancePatch> patchCaptor;

	@Captor
	private ArgumentCaptor<SearchFilter> searchFilterCaptor;

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
		when(comfactIntegrationMock.createSigningInstance(any(SigningInstanceInput.class)))
			.thenReturn(new generated.se.sundsvall.comfact.SigningInstance()
				.signingInstanceId("123")
				.signatories(List.of(new generated.se.sundsvall.comfact.Signatory()
					.partyId(partyId)
					.signatoryUrl("someUrl"))));

		// Act
		final var result = signingService.createSigningRequest(MUNICIPALITY_ID, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo("123");
		assertThat(result.getSignatoryUrls()).hasSize(1);
		assertThat(result.getSignatoryUrls()).containsEntry("partyId", "someUrl");
		verify(comfactIntegrationMock).createSigningInstance(inputCaptor.capture());
		assertThat(inputCaptor.getValue()).isNotNull();
		assertThat(inputCaptor.getValue().getSignatories()).hasSize(1).satisfies(
			signatories -> assertThat(signatories.getFirst().getPartyId()).isEqualTo(partyId));
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
		verify(comfactIntegrationMock).updateSigningInstance(eq(signingId), patchCaptor.capture());
		assertThat(patchCaptor.getValue()).isNotNull();
	}

	@Test
	void cancelSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";

		// Act
		signingService.cancelSigningRequest(MUNICIPALITY_ID, signingId);

		// Assert
		verify(comfactIntegrationMock).withdrawSigningInstance(signingId);
	}

	@Test
	void getSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var response = new generated.se.sundsvall.comfact.SigningInstance()
			.signingInstanceId(signingId)
			.document(new generated.se.sundsvall.comfact.Document()
				.content("someContent".getBytes(StandardCharsets.UTF_8)))
			.status(generated.se.sundsvall.comfact.Status.ACTIVE);

		when(comfactIntegrationMock.getSigningInstance(signingId)).thenReturn(response);

		// Act
		final var result = signingService.getSigningRequest(MUNICIPALITY_ID, signingId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo(signingId);
		verify(comfactIntegrationMock).getSigningInstance(signingId);
	}

	@Test
	void getSigningRequests() {
		// Arrange
		final var searchResult = new SearchResult()
			.signingInstanceInfos(List.of(
				new generated.se.sundsvall.comfact.SigningInstanceInfo().signingInstanceId("123").status(generated.se.sundsvall.comfact.Status.ACTIVE),
				new generated.se.sundsvall.comfact.SigningInstanceInfo().signingInstanceId("456").status(generated.se.sundsvall.comfact.Status.ACTIVE)))
			.paginator(new Paginator().page(0).pageSize(2).orderByProperty(Property.CREATED).orderByDescending(true));

		when(comfactIntegrationMock.searchSigningInstanceInfos(any(SearchFilter.class))).thenReturn(searchResult);
		when(pageableMock.getPageNumber()).thenReturn(0);
		when(pageableMock.getPageSize()).thenReturn(2);
		when(pageableMock.getSort()).thenReturn(Sort.by(Sort.Order.asc("created")));

		// Act
		final var result = signingService.getSigningRequests(MUNICIPALITY_ID, pageableMock);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningInstances()).hasSize(2);
		assertThat(result.getSigningInstances().getFirst().getSigningId()).isEqualTo("123");
		assertThat(result.getSigningInstances().getLast().getSigningId()).isEqualTo("456");

		verify(comfactIntegrationMock).searchSigningInstanceInfos(searchFilterCaptor.capture());
		assertThat(searchFilterCaptor.getValue()).isNotNull();
		assertThat(searchFilterCaptor.getValue().getPaginator()).isNotNull();
	}

	@Test
	void getSignatory() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		when(comfactIntegrationMock.getSignatory(signingId, partyId))
			.thenReturn(new generated.se.sundsvall.comfact.Signatory().partyId(partyId));

		// Act
		final var result = signingService.getSignatory(MUNICIPALITY_ID, signingId, partyId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getPartyId()).isEqualTo(partyId);
		verify(comfactIntegrationMock).getSignatory(signingId, partyId);
	}
}
