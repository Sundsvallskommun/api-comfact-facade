package se.sundsvall.comfactfacade.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class PartyTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Party.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void constructorAndGetter() {
		// Arrange
		final var name = "someName";
		final var email = "someEmail";
		final var phone = "somePhone";
		final var partyId = "somePartyId";
		final var title = "someTitle";
		final var personalNumber = "somePersonNumber";
		final var organization = "someOrganization";
		final var language = "someLanguage";
		final var identification = List.of(new Identification("someAlias"));
		final var notificationMessage = new NotificationMessage("defaultSubject", "defaultBody", "sv-SE");

		// Act
		final var result = new Party(name, partyId, notificationMessage, title, personalNumber, email, phone, organization, language, identification);

		// Assert
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getPersonalNumber()).isEqualTo(personalNumber);
		assertThat(result.getEmail()).isEqualTo(email);
		assertThat(result.getPhoneNumber()).isEqualTo(phone);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getLanguage()).isEqualTo(language);
		assertThat(result.getIdentifications()).isEqualTo(identification);
		assertThat(result.getNotificationMessage()).isEqualTo(notificationMessage);
	}


	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Party.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Party()).hasAllNullFieldsOrProperties();
	}

}
