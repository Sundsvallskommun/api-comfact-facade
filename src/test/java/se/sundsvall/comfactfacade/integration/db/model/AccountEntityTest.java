package se.sundsvall.comfactfacade.integration.db.model;

import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.CoreMatchers.allOf;

class AccountEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(AccountEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var id = "id";
		final var municipalityId = "2281";
		final var accountKey = "accountKey";
		final var comfactAccountId = "comfactAccountId";
		final var clientId = "clientId";
		final var clientSecret = "clientSecret";
		final var description = "description";
		final var created = OffsetDateTime.now().minusDays(1);
		final var modified = OffsetDateTime.now();

		final var entity = AccountEntity.create()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withAccountKey(accountKey)
			.withComfactAccountId(comfactAccountId)
			.withClientId(clientId)
			.withClientSecret(clientSecret)
			.withDescription(description)
			.withDefaultAccount(true)
			.withCreated(created)
			.withModified(modified);

		assertThat(entity).satisfies(e -> {
			assertThat(e.getId()).isEqualTo(id);
			assertThat(e.getMunicipalityId()).isEqualTo(municipalityId);
			assertThat(e.getAccountKey()).isEqualTo(accountKey);
			assertThat(e.getComfactAccountId()).isEqualTo(comfactAccountId);
			assertThat(e.getClientId()).isEqualTo(clientId);
			assertThat(e.getClientSecret()).isEqualTo(clientSecret);
			assertThat(e.getDescription()).isEqualTo(description);
			assertThat(e.isDefaultAccount()).isTrue();
			assertThat(e.getCreated()).isEqualTo(created);
			assertThat(e.getModified()).isEqualTo(modified);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testOnCreate() {
		final var entity = AccountEntity.create();
		entity.onCreate();

		assertThat(entity.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, java.time.temporal.ChronoUnit.SECONDS));
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created", "defaultAccount");
	}

	@Test
	void testOnUpdate() {
		final var entity = AccountEntity.create();
		entity.onUpdate();

		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, java.time.temporal.ChronoUnit.SECONDS));
		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("modified", "defaultAccount");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AccountEntity.create()).hasAllNullFieldsOrPropertiesExcept("defaultAccount");
		assertThat(new AccountEntity()).hasAllNullFieldsOrPropertiesExcept("defaultAccount");
	}
}
