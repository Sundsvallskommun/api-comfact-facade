package se.sundsvall.comfactfacade.api.model;

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
import static org.hamcrest.CoreMatchers.allOf;

class AccountTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(Account.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var id = "id";
		final var accountKey = "accountKey";
		final var comfactAccountId = "comfactAccountId";
		final var clientId = "clientId";
		final var clientSecret = "clientSecret";
		final var description = "description";
		final var created = OffsetDateTime.now().minusDays(1);
		final var modified = OffsetDateTime.now();

		final var bean = Account.builder()
			.withId(id)
			.withAccountKey(accountKey)
			.withComfactAccountId(comfactAccountId)
			.withClientId(clientId)
			.withClientSecret(clientSecret)
			.withDescription(description)
			.withDefaultAccount(true)
			.withCreated(created)
			.withModified(modified)
			.build();

		assertThat(bean).satisfies(b -> {
			assertThat(b.getId()).isEqualTo(id);
			assertThat(b.getAccountKey()).isEqualTo(accountKey);
			assertThat(b.getComfactAccountId()).isEqualTo(comfactAccountId);
			assertThat(b.getClientId()).isEqualTo(clientId);
			assertThat(b.getClientSecret()).isEqualTo(clientSecret);
			assertThat(b.getDescription()).isEqualTo(description);
			assertThat(b.getDefaultAccount()).isTrue();
			assertThat(b.getCreated()).isEqualTo(created);
			assertThat(b.getModified()).isEqualTo(modified);
		}).hasNoNullFieldsOrProperties();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Account.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Account()).hasAllNullFieldsOrProperties();
	}
}
