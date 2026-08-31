package se.sundsvall.comfactfacade.service;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import se.sundsvall.comfactfacade.api.model.Account;
import se.sundsvall.comfactfacade.integration.db.model.AccountEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

	@Test
	void toAccount() {
		final var created = OffsetDateTime.now().minusDays(1);
		final var modified = OffsetDateTime.now();
		final var entity = AccountEntity.create()
			.withId("id")
			.withMunicipalityId("2281")
			.withAccountKey("accountKey")
			.withComfactAccountId("comfactAccountId")
			.withClientId("clientId")
			.withClientSecret("encryptedSecret")
			.withDescription("description")
			.withDefaultAccount(true)
			.withCreated(created)
			.withModified(modified);

		final var result = AccountMapper.toAccount(entity);

		assertThat(result.getId()).isEqualTo("id");
		assertThat(result.getAccountKey()).isEqualTo("accountKey");
		assertThat(result.getComfactAccountId()).isEqualTo("comfactAccountId");
		assertThat(result.getClientId()).isEqualTo("clientId");
		assertThat(result.getClientSecret()).isNull();
		assertThat(result.getDescription()).isEqualTo("description");
		assertThat(result.getDefaultAccount()).isTrue();
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getModified()).isEqualTo(modified);
	}

	@Test
	void toAccounts() {
		final var result = AccountMapper.toAccounts(java.util.List.of(
			AccountEntity.create().withId("1"),
			AccountEntity.create().withId("2")));

		assertThat(result).extracting(Account::getId).containsExactly("1", "2");
	}

	@Test
	void toAccountsWithNull() {
		assertThat(AccountMapper.toAccounts(null)).isEmpty();
	}

	@Test
	void toAccountEntity() {
		final var account = Account.builder()
			.withId("ignored")
			.withAccountKey("accountKey")
			.withComfactAccountId("comfactAccountId")
			.withClientId("clientId")
			.withClientSecret("clientSecret")
			.withDescription("description")
			.build();

		final var result = AccountMapper.toAccountEntity("2281", account);

		assertThat(result.getId()).isNull();
		assertThat(result.getMunicipalityId()).isEqualTo("2281");
		assertThat(result.getAccountKey()).isEqualTo("accountKey");
		assertThat(result.getComfactAccountId()).isEqualTo("comfactAccountId");
		assertThat(result.getClientId()).isEqualTo("clientId");
		assertThat(result.getClientSecret()).isNull();
		assertThat(result.getDescription()).isEqualTo("description");
		assertThat(result.isDefaultAccount()).isFalse();
		assertThat(result.getCreated()).isNull();
		assertThat(result.getModified()).isNull();
	}

	@Test
	void toAccountEntityWithDefaultAccount() {
		final var result = AccountMapper.toAccountEntity("2281", Account.builder()
			.withAccountKey("accountKey")
			.withComfactAccountId("comfactAccountId")
			.withDefaultAccount(true)
			.build());

		assertThat(result.isDefaultAccount()).isTrue();
	}

	@Test
	void updateAccountEntity() {
		final var entity = AccountEntity.create()
			.withAccountKey("oldKey")
			.withComfactAccountId("oldAccountId")
			.withClientId("oldClientId")
			.withDescription("oldDescription")
			.withDefaultAccount(true);

		final var result = AccountMapper.updateAccountEntity(entity, Account.builder()
			.withComfactAccountId("newAccountId")
			.build());

		assertThat(result.getAccountKey()).isEqualTo("oldKey");
		assertThat(result.getComfactAccountId()).isEqualTo("newAccountId");
		assertThat(result.getClientId()).isEqualTo("oldClientId");
		assertThat(result.getDescription()).isEqualTo("oldDescription");
		assertThat(result.isDefaultAccount()).isTrue();
	}

	@Test
	void updateAccountEntityAllFields() {
		final var entity = AccountEntity.create()
			.withAccountKey("oldKey")
			.withComfactAccountId("oldAccountId")
			.withDescription("oldDescription");

		final var result = AccountMapper.updateAccountEntity(entity, Account.builder()
			.withAccountKey("newKey")
			.withComfactAccountId("newAccountId")
			.withDescription("newDescription")
			.build());

		assertThat(result.getAccountKey()).isEqualTo("newKey");
		assertThat(result.getComfactAccountId()).isEqualTo("newAccountId");
		assertThat(result.getDescription()).isEqualTo("newDescription");
	}
}
