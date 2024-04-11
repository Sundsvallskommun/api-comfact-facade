package se.sundsvall.comfactfacade.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SigningRequestTest {

	@BeforeAll
	static void setUp() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void bean() {
		MatcherAssert.assertThat(SigningRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var customerReference = "someCustomerReference";
		final var expires = now();
		final var notificationMessage = new NotificationMessage("someSubject", "someBody", "sv-SE");
		final var party =
			Party.builder()
				.withName("someName")
				.withPartyId("somePartyId")
				.withNotificationMessage(notificationMessage)
				.withTitle("someTitle")
				.withEmail("someEmail")
				.withPhoneNumber("somePhone")
				.withOrganization("some")
				.withLanguage("someLanguage")
				.build();

		final var signatory =
			Signatory.builder()
				.withName("someName")
				.withPartyId("somePartyId")
				.withNotificationMessage(notificationMessage)
				.withTitle("someTitle")
				.withEmail("someEmail")
				.withPhoneNumber("somePhone")
				.withOrganization("some")
				.withLanguage("someLanguage")
				.withIdentifications(List.of(new Identification("someAlias")))
				.build();
		final var signatories = List.of(signatory);
		final var document = new Document("someName", "someFileName", "someContentType", "someContent");
		final var additionalDocuments = List.of(document, new Document("someName", "someFileName", "someContentType", "someContent"));
		final var language = "someLanguage";
		final var reminder = new Reminder(notificationMessage, true, 24, now());

		// Act
		final var result = SigningRequest.builder()
			.withAdditionalDocuments(additionalDocuments)
			.withAdditionalParty(party)
			.withCustomerReference(customerReference)
			.withDocument(document)
			.withExpires(expires)
			.withInitiator(party)
			.withLanguage(language)
			.withNotificationMessage(notificationMessage)
			.withReminder(reminder)
			.withSignatories(signatories)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getCustomerReference()).isEqualTo(customerReference);
		assertThat(result.getExpires()).isEqualTo(expires);
		assertThat(result.getNotificationMessage()).isSameAs(notificationMessage);
		assertThat(result.getSignatories()).isSameAs(signatories);
		assertThat(result.getAdditionalParty()).isSameAs(party);
		assertThat(result.getInitiator()).isEqualTo(party);
		assertThat(result.getDocument()).isSameAs(document);
		assertThat(result.getAdditionalDocuments()).isEqualTo(additionalDocuments);
		assertThat(result.getLanguage()).isEqualTo(language);
		assertThat(result.getReminder()).isSameAs(reminder);

	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(SigningRequest.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SigningRequest()).hasAllNullFieldsOrProperties();
	}

}
