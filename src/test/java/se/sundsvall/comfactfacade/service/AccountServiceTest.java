package se.sundsvall.comfactfacade.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.comfactfacade.api.model.Account;
import se.sundsvall.comfactfacade.integration.db.AccountRepository;
import se.sundsvall.comfactfacade.integration.db.model.AccountEntity;
import se.sundsvall.comfactfacade.utility.EncryptionUtility;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
	private AccountRepository accountRepositoryMock;

	@Mock
	private EncryptionUtility encryptionUtilityMock;

	@InjectMocks
	private AccountService accountService;

	@Captor
	private ArgumentCaptor<AccountEntity> entityCaptor;

	@Test
	void getAccounts() {
		// Arrange
		when(accountRepositoryMock.findAllByMunicipalityId(MUNICIPALITY_ID)).thenReturn(List.of(
			AccountEntity.create().withId("1"),
			AccountEntity.create().withId("2")));

		// Act
		final var result = accountService.getAccounts(MUNICIPALITY_ID);

		// Assert
		assertThat(result).extracting(Account::getId).containsExactly("1", "2");
	}

	@Test
	void getAccount() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.of(
			AccountEntity.create().withId("id").withAccountKey("accountKey")));

		// Act
		final var result = accountService.getAccount(MUNICIPALITY_ID, "id");

		// Assert
		assertThat(result.getId()).isEqualTo("id");
		assertThat(result.getAccountKey()).isEqualTo("accountKey");
	}

	@Test
	void getAccountNotFound() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> accountService.getAccount(MUNICIPALITY_ID, "id"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void createAccount() {
		// Arrange
		final var account = Account.builder()
			.withAccountKey("accountKey")
			.withComfactAccountId("comfactAccountId")
			.withClientId("clientId")
			.withClientSecret("clientSecret")
			.withDescription("description")
			.build();

		when(accountRepositoryMock.existsByMunicipalityIdAndAccountKey(MUNICIPALITY_ID, "accountKey")).thenReturn(false);
		when(encryptionUtilityMock.encrypt("clientSecret".getBytes())).thenReturn("encryptedSecret");
		when(accountRepositoryMock.save(any(AccountEntity.class))).thenAnswer(invocation -> ((AccountEntity) invocation.getArgument(0)).withId("generated-id"));

		// Act
		final var result = accountService.createAccount(MUNICIPALITY_ID, account);

		// Assert
		assertThat(result).isEqualTo("generated-id");
		verify(accountRepositoryMock).save(entityCaptor.capture());
		assertThat(entityCaptor.getValue().getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(entityCaptor.getValue().getAccountKey()).isEqualTo("accountKey");
		assertThat(entityCaptor.getValue().getComfactAccountId()).isEqualTo("comfactAccountId");
		assertThat(entityCaptor.getValue().getClientId()).isEqualTo("clientId");
		assertThat(entityCaptor.getValue().getClientSecret()).isEqualTo("encryptedSecret");
	}

	@Test
	void createAccountDuplicateKey() {
		// Arrange
		final var account = Account.builder().withAccountKey("accountKey").build();
		when(accountRepositoryMock.existsByMunicipalityIdAndAccountKey(MUNICIPALITY_ID, "accountKey")).thenReturn(true);

		// Act & Assert
		assertThatThrownBy(() -> accountService.createAccount(MUNICIPALITY_ID, account))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", CONFLICT);
		verify(accountRepositoryMock, never()).save(any());
	}

	@Test
	void updateAccount() {
		// Arrange
		final var entity = AccountEntity.create().withId("id").withAccountKey("oldKey").withComfactAccountId("oldAccountId");
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.of(entity));

		// Act
		accountService.updateAccount(MUNICIPALITY_ID, "id", Account.builder().withComfactAccountId("newAccountId").build());

		// Assert
		verify(accountRepositoryMock).save(entityCaptor.capture());
		assertThat(entityCaptor.getValue().getAccountKey()).isEqualTo("oldKey");
		assertThat(entityCaptor.getValue().getComfactAccountId()).isEqualTo("newAccountId");
	}

	@Test
	void updateAccountWithNewClientSecret() {
		// Arrange
		final var entity = AccountEntity.create().withId("id").withClientSecret("oldEncrypted");
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.of(entity));
		when(encryptionUtilityMock.encrypt("newSecret".getBytes())).thenReturn("newEncrypted");

		// Act
		accountService.updateAccount(MUNICIPALITY_ID, "id", Account.builder().withClientSecret("newSecret").build());

		// Assert
		verify(accountRepositoryMock).save(entityCaptor.capture());
		assertThat(entityCaptor.getValue().getClientSecret()).isEqualTo("newEncrypted");
	}

	@Test
	void updateAccountNotFound() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> accountService.updateAccount(MUNICIPALITY_ID, "id", Account.builder().build()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
		verify(accountRepositoryMock, never()).save(any());
	}

	@Test
	void deleteAccount() {
		// Arrange
		final var entity = AccountEntity.create().withId("id");
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.of(entity));

		// Act
		accountService.deleteAccount(MUNICIPALITY_ID, "id");

		// Assert
		verify(accountRepositoryMock).delete(entity);
	}

	@Test
	void deleteAccountNotFound() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> accountService.deleteAccount(MUNICIPALITY_ID, "id"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
		verify(accountRepositoryMock, never()).delete(any(AccountEntity.class));
	}

	@Test
	void createAccountAsDefaultClearsPreviousDefault() {
		// Arrange
		final var previousDefault = AccountEntity.create().withId("old-default").withDefaultAccount(true);
		final var account = Account.builder()
			.withAccountKey("accountKey")
			.withComfactAccountId("comfactAccountId")
			.withClientId("clientId")
			.withClientSecret("clientSecret")
			.withDefaultAccount(true)
			.build();

		when(accountRepositoryMock.existsByMunicipalityIdAndAccountKey(MUNICIPALITY_ID, "accountKey")).thenReturn(false);
		when(encryptionUtilityMock.encrypt("clientSecret".getBytes())).thenReturn("encryptedSecret");
		when(accountRepositoryMock.findByMunicipalityIdAndDefaultAccountTrue(MUNICIPALITY_ID)).thenReturn(Optional.of(previousDefault));
		when(accountRepositoryMock.save(any(AccountEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		accountService.createAccount(MUNICIPALITY_ID, account);

		// Assert
		verify(accountRepositoryMock, times(2)).save(entityCaptor.capture());
		assertThat(entityCaptor.getAllValues().getFirst().getId()).isEqualTo("old-default");
		assertThat(entityCaptor.getAllValues().getFirst().isDefaultAccount()).isFalse();
		assertThat(entityCaptor.getAllValues().getLast().isDefaultAccount()).isTrue();
	}

	@Test
	void updateAccountToDefaultClearsPreviousDefault() {
		// Arrange
		final var previousDefault = AccountEntity.create().withId("old-default").withDefaultAccount(true);
		final var entity = AccountEntity.create().withId("id").withDefaultAccount(false);

		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.of(entity));
		when(accountRepositoryMock.findByMunicipalityIdAndDefaultAccountTrue(MUNICIPALITY_ID)).thenReturn(Optional.of(previousDefault));

		// Act
		accountService.updateAccount(MUNICIPALITY_ID, "id", Account.builder().withDefaultAccount(true).build());

		// Assert
		verify(accountRepositoryMock, times(2)).save(entityCaptor.capture());
		assertThat(entityCaptor.getAllValues().getFirst().getId()).isEqualTo("old-default");
		assertThat(entityCaptor.getAllValues().getFirst().isDefaultAccount()).isFalse();
		assertThat(entityCaptor.getAllValues().getLast().getId()).isEqualTo("id");
		assertThat(entityCaptor.getAllValues().getLast().isDefaultAccount()).isTrue();
	}

	@Test
	void updateAccountAlreadyDefaultDoesNotClear() {
		// Arrange
		final var entity = AccountEntity.create().withId("id").withDefaultAccount(true);
		when(accountRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, "id")).thenReturn(Optional.of(entity));

		// Act
		accountService.updateAccount(MUNICIPALITY_ID, "id", Account.builder().withDefaultAccount(true).build());

		// Assert
		verify(accountRepositoryMock, never()).findByMunicipalityIdAndDefaultAccountTrue(any());
		verify(accountRepositoryMock).save(entity);
	}

	@Test
	void resolveAccountByKey() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndAccountKey(MUNICIPALITY_ID, "accountKey")).thenReturn(Optional.of(
			AccountEntity.create().withId("id").withClientId("clientId").withClientSecret("encrypted").withComfactAccountId("comfactAccountId")));
		when(encryptionUtilityMock.decrypt("encrypted")).thenReturn("decrypted");

		// Act
		final var result = accountService.resolveAccount(MUNICIPALITY_ID, "accountKey");

		// Assert
		assertThat(result.id()).isEqualTo("id");
		assertThat(result.clientId()).isEqualTo("clientId");
		assertThat(result.clientSecret()).isEqualTo("decrypted");
		assertThat(result.comfactAccountId()).isEqualTo("comfactAccountId");
	}

	@Test
	void resolveAccountByUnknownKey() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndAccountKey(MUNICIPALITY_ID, "unknown-key")).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> accountService.resolveAccount(MUNICIPALITY_ID, "unknown-key"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST)
			.hasMessageContaining("unknown-key");
	}

	@Test
	void resolveAccountWithoutKeyUsesDefault() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndDefaultAccountTrue(MUNICIPALITY_ID)).thenReturn(Optional.of(
			AccountEntity.create().withId("id").withClientId("clientId").withClientSecret("encrypted")));
		when(encryptionUtilityMock.decrypt("encrypted")).thenReturn("decrypted");

		// Act
		final var result = accountService.resolveAccount(MUNICIPALITY_ID, null);

		// Assert
		assertThat(result.clientSecret()).isEqualTo("decrypted");
		assertThat(result.comfactAccountId()).isNull();
	}

	@Test
	void resolveAccountWithBlankKeyAndNoDefault() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndDefaultAccountTrue(MUNICIPALITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> accountService.resolveAccount(MUNICIPALITY_ID, " "))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST)
			.hasMessageContaining("No default account is configured");
	}

	@Test
	void resolveAccountWithoutKeyAndNoDefault() {
		// Arrange
		when(accountRepositoryMock.findByMunicipalityIdAndDefaultAccountTrue(MUNICIPALITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> accountService.resolveAccount(MUNICIPALITY_ID, null))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST)
			.hasMessageContaining("No default account is configured");
	}
}
