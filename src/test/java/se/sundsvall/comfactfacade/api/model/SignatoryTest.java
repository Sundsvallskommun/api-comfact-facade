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

class SignatoryTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Signatory.class, allOf(
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
		final var identifications = List.of(Identification.builder().withAlias("SvensktEId").build());

		// Act
		final var result = Signatory.builder()
			.withName(name)
			.withEmail(email)
			.withPhoneNumber(phone)
			.withPartyId(partyId)
			.withTitle(title)
			.withOrganization(organization)
			.withLanguage(language)
			.withNotificationMessage(notificationMessage)
			.withIdentifications(identifications)
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
		assertThat(result.getIdentifications()).isSameAs(identifications);
	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(Signatory.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Signatory()).hasAllNullFieldsOrProperties();
	}

}
