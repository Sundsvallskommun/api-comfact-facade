package se.sundsvall.comfactfacade.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

class PartyTest {

	@Test
	void bean() {
		MatcherAssert.assertThat(Party.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var name = "someName";
		final var email = "someEmail";
		final var phone = "somePhone";
		final var partyId = "somePartyId";
		final var title = "someTitle";
		final var organization = "someOrganization";
		final var language = "someLanguage";
		final var notificationMessage = new NotificationMessage("defaultSubject", "defaultBody", "sv-SE");

		// Act
		final var result = Party.builder()
			.withName(name)
			.withEmail(email)
			.withPhoneNumber(phone)
			.withPartyId(partyId)
			.withTitle(title)
			.withOrganization(organization)
			.withLanguage(language)
			.withNotificationMessage(notificationMessage)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getEmail()).isEqualTo(email);
		assertThat(result.getPhoneNumber()).isEqualTo(phone);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getLanguage()).isEqualTo(language);
		assertThat(result.getNotificationMessage()).isSameAs(notificationMessage);
	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(Party.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Party()).hasAllNullFieldsOrProperties();
	}

}
