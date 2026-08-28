package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.Document;
import generated.se.sundsvall.comfact.Paginator;
import generated.se.sundsvall.comfact.Property;
import generated.se.sundsvall.comfact.SearchFilter;
import generated.se.sundsvall.comfact.SearchResult;
import generated.se.sundsvall.comfact.SigningInstance;
import generated.se.sundsvall.comfact.SigningInstanceInfo;
import generated.se.sundsvall.comfact.SigningInstanceInput;
import generated.se.sundsvall.comfact.SigningInstancePatch;
import generated.se.sundsvall.comfact.Status;
import generated.se.sundsvall.comfact.StatusPatch;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
import se.sundsvall.comfactfacade.api.model.UpdateSigningRequest;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;
import se.sundsvall.comfactfacade.integration.party.PartyClient;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

	@Mock
	private PartyClient partyClientMock;

	@Mock
	private AccountService accountServiceMock;

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

		when(accountServiceMock.resolveAccount(MUNICIPALITY_ID, null)).thenReturn(new AccountCredentials("id", "clientId", "clientSecret", null));
		when(partyClientMock.getLegalIds(MUNICIPALITY_ID, List.of(partyId))).thenReturn(Map.of(partyId, "someLegalId"));
		when(comfactIntegrationMock.createSigningInstance(any(SigningInstanceInput.class)))
			.thenReturn(new SigningInstance()
				.signingInstanceId("123")
				.signatories(List.of(new generated.se.sundsvall.comfact.Signatory()
					.partyId(partyId)
					.signatoryUrl("someUrl"))));

		// Act
		final var result = signingService.createSigningRequest(MUNICIPALITY_ID, null, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSigningId()).isEqualTo("123");
		assertThat(result.getSignatoryUrls()).hasSize(1);
		assertThat(result.getSignatoryUrls()).containsEntry("partyId", "someUrl");
		verify(comfactIntegrationMock).createSigningInstance(inputCaptor.capture());
		assertThat(inputCaptor.getValue()).isNotNull();
		assertThat(inputCaptor.getValue().getAccountId()).isNull();
		assertThat(inputCaptor.getValue().getSignatories()).hasSize(1).satisfies(
			signatories -> assertThat(signatories.getFirst().getPartyId()).isEqualTo(partyId));
		verify(accountServiceMock).resolveAccount(MUNICIPALITY_ID, null);
		verifyNoMoreInteractions(accountServiceMock);
	}

	@Test
	void createSigningRequestWithDefaultAccount() {
		// Arrange
		final var request = SigningRequest.builder().build();

		when(accountServiceMock.resolveAccount(MUNICIPALITY_ID, null))
			.thenReturn(new AccountCredentials("id", "clientId", "clientSecret", "comfact-account-1"));
		when(comfactIntegrationMock.createSigningInstance(any(SigningInstanceInput.class)))
			.thenReturn(new SigningInstance().signingInstanceId("123").signatories(List.of()));

		// Act
		signingService.createSigningRequest(MUNICIPALITY_ID, null, request);

		// Assert
		verify(comfactIntegrationMock).createSigningInstance(inputCaptor.capture());
		assertThat(inputCaptor.getValue().getAccountId()).isEqualTo("comfact-account-1");
	}

	@Test
	void createSigningRequestWithAccountWithoutComfactAccountId() {
		// Arrange
		final var request = SigningRequest.builder().build();

		when(accountServiceMock.resolveAccount(MUNICIPALITY_ID, null))
			.thenReturn(new AccountCredentials("id", "clientId", "clientSecret", null));
		when(comfactIntegrationMock.createSigningInstance(any(SigningInstanceInput.class)))
			.thenReturn(new SigningInstance().signingInstanceId("123").signatories(List.of()));

		// Act
		signingService.createSigningRequest(MUNICIPALITY_ID, null, request);

		// Assert
		verify(comfactIntegrationMock).createSigningInstance(inputCaptor.capture());
		assertThat(inputCaptor.getValue().getAccountId()).isNull();
	}

	@Test
	void createSigningRequestWithAccountKey() {
		// Arrange
		final var accountKey = "social-services";
		final var comfactAccountId = "comfact-account-2";
		final var request = SigningRequest.builder().build();

		when(accountServiceMock.resolveAccount(MUNICIPALITY_ID, accountKey))
			.thenReturn(new AccountCredentials("id", "clientId", "clientSecret", comfactAccountId));
		when(comfactIntegrationMock.createSigningInstance(any(SigningInstanceInput.class)))
			.thenReturn(new SigningInstance().signingInstanceId("123").signatories(List.of()));

		// Act
		final var result = signingService.createSigningRequest(MUNICIPALITY_ID, accountKey, request);

		// Assert
		assertThat(result).isNotNull();
		verify(accountServiceMock).resolveAccount(MUNICIPALITY_ID, accountKey);
		verify(comfactIntegrationMock).createSigningInstance(inputCaptor.capture());
		assertThat(inputCaptor.getValue().getAccountId()).isEqualTo(comfactAccountId);
	}

	@Test
	void createSigningRequestWithBlankAccountKey() {
		// Arrange
		final var request = SigningRequest.builder().build();

		when(accountServiceMock.resolveAccount(MUNICIPALITY_ID, " ")).thenReturn(new AccountCredentials("id", "clientId", "clientSecret", null));
		when(comfactIntegrationMock.createSigningInstance(any(SigningInstanceInput.class)))
			.thenReturn(new SigningInstance().signingInstanceId("123").signatories(List.of()));

		// Act
		signingService.createSigningRequest(MUNICIPALITY_ID, " ", request);

		// Assert
		verify(comfactIntegrationMock).createSigningInstance(inputCaptor.capture());
		assertThat(inputCaptor.getValue().getAccountId()).isNull();
		verify(accountServiceMock).resolveAccount(MUNICIPALITY_ID, " ");
		verifyNoMoreInteractions(accountServiceMock);
	}

	@Test
	void createSigningRequestWithUnknownAccountKey() {
		// Arrange
		final var accountKey = "unknown-key";
		final var request = SigningRequest.builder().build();

		when(accountServiceMock.resolveAccount(MUNICIPALITY_ID, accountKey))
			.thenThrow(Problem.valueOf(BAD_REQUEST, "No account found for municipalityId '%s' and account key '%s'".formatted(MUNICIPALITY_ID, accountKey)));

		// Act & Assert
		assertThatThrownBy(() -> signingService.createSigningRequest(MUNICIPALITY_ID, accountKey, request))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
		verifyNoInteractions(comfactIntegrationMock);
	}

	@Test
	void updateSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var updateRequest = UpdateSigningRequest.builder()
			.withExpires(java.time.OffsetDateTime.now())
			.withStatus("active")
			.build();

		// Act
		signingService.updateSigningRequest(MUNICIPALITY_ID, null, signingId, updateRequest);

		// Assert
		verify(comfactIntegrationMock).updateSigningInstance(eq(signingId), patchCaptor.capture());
		assertThat(patchCaptor.getValue()).isNotNull();
		assertThat(patchCaptor.getValue().getExpires()).isNotNull();
		assertThat(patchCaptor.getValue().getStatus()).isEqualTo(StatusPatch.ACTIVE);
	}

	@Test
	void getSigningRequest() {
		// Arrange
		final var signingId = "someSigningId";
		final var response = new SigningInstance()
			.signingInstanceId(signingId)
			.document(new Document()
				.content("someContent".getBytes(StandardCharsets.UTF_8)))
			.status(Status.ACTIVE);

		when(comfactIntegrationMock.getSigningInstance(signingId)).thenReturn(response);

		// Act
		final var result = signingService.getSigningRequest(MUNICIPALITY_ID, null, signingId);

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
				new SigningInstanceInfo().signingInstanceId("123").status(Status.ACTIVE),
				new SigningInstanceInfo().signingInstanceId("456").status(Status.ACTIVE)))
			.paginator(new Paginator().page(0).pageSize(2).orderByProperty(Property.CREATED).orderByDescending(true));

		when(comfactIntegrationMock.searchSigningInstanceInfos(any(SearchFilter.class))).thenReturn(searchResult);
		when(pageableMock.getPageNumber()).thenReturn(0);
		when(pageableMock.getPageSize()).thenReturn(2);
		when(pageableMock.getSort()).thenReturn(Sort.by(Sort.Order.asc("created")));

		// Act
		final var result = signingService.getSigningRequests(MUNICIPALITY_ID, null, pageableMock);

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
		final var result = signingService.getSignatory(MUNICIPALITY_ID, null, signingId, partyId);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getPartyId()).isEqualTo(partyId);
		verify(comfactIntegrationMock).getSignatory(signingId, partyId);
	}
}
