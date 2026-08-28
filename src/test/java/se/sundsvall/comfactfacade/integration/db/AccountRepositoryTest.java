package se.sundsvall.comfactfacade.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.comfactfacade.integration.db.model.AccountEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class AccountRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void findByMunicipalityIdAndAccountKey() {
		final var result = accountRepository.findByMunicipalityIdAndAccountKey("2281", "social-services");

		assertThat(result).hasValueSatisfying(entity -> {
			assertThat(entity.getId()).isEqualTo("5a1efd51-1b1e-4a20-9673-6b6f79f27f02");
			assertThat(entity.getComfactAccountId()).isEqualTo("comfact-account-2");
		});
	}

	@Test
	void findByMunicipalityIdAndAccountKeyNotFound() {
		assertThat(accountRepository.findByMunicipalityIdAndAccountKey("2281", "unknown-key")).isEmpty();
	}

	@Test
	void findByMunicipalityIdAndAccountKeyWrongMunicipality() {
		assertThat(accountRepository.findByMunicipalityIdAndAccountKey("2262", "social-services")).isEmpty();
	}

	@Test
	void findByMunicipalityIdAndId() {
		final var result = accountRepository.findByMunicipalityIdAndId("2281", "5a1efd51-1b1e-4a20-9673-6b6f79f27f01");

		assertThat(result).hasValueSatisfying(entity -> assertThat(entity.getAccountKey()).isEqualTo("default-unit"));
	}

	@Test
	void findByMunicipalityIdAndIdNotFound() {
		assertThat(accountRepository.findByMunicipalityIdAndId("2262", "5a1efd51-1b1e-4a20-9673-6b6f79f27f01")).isEmpty();
	}

	@Test
	void findAllByMunicipalityId() {
		final var result = accountRepository.findAllByMunicipalityId("2281");

		assertThat(result)
			.extracting(AccountEntity::getAccountKey)
			.containsExactlyInAnyOrder("default-unit", "social-services");
	}

	@Test
	void existsByMunicipalityIdAndAccountKey() {
		assertThat(accountRepository.existsByMunicipalityIdAndAccountKey("2281", "default-unit")).isTrue();
		assertThat(accountRepository.existsByMunicipalityIdAndAccountKey("2281", "unknown-key")).isFalse();
	}

	@Test
	void findByMunicipalityIdAndDefaultAccountTrue() {
		final var result = accountRepository.findByMunicipalityIdAndDefaultAccountTrue("2262");

		assertThat(result).hasValueSatisfying(entity -> {
			assertThat(entity.getId()).isEqualTo("5a1efd51-1b1e-4a20-9673-6b6f79f27f03");
			assertThat(entity.getComfactAccountId()).isEqualTo("comfact-account-3");
		});
	}

	@Test
	void findByMunicipalityIdAndDefaultAccountTrueNoDefault() {
		assertThat(accountRepository.findByMunicipalityIdAndDefaultAccountTrue("1984")).isEmpty();
	}

	@Test
	void persistSetsIdAndCreated() {
		final var entity = accountRepository.saveAndFlush(AccountEntity.create()
			.withMunicipalityId("2281")
			.withAccountKey("new-key")
			.withComfactAccountId("comfact-account-9")
			.withClientId("client-nine")
			.withClientSecret("encrypted-secret"));

		assertThat(entity.getId()).isNotBlank();
		assertThat(entity.getCreated()).isNotNull();
		assertThat(entity.getModified()).isNull();
	}
}
