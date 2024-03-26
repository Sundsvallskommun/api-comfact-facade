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

class SigningInstanceTest {

	@BeforeAll
	static void setUp() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void bean() {
		MatcherAssert.assertThat(SigningInstance.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var notificationMessage = List.of(new NotificationMessage());
		final var initiator = new Party();
		final var additionalParties = List.of(new Party());
		final var signatories = List.of(new Party());
		final var status = new Status();
		final var customerReference = "customerReference";
		final var created = OffsetDateTime.now();
		final var changed = OffsetDateTime.now();
		final var expires = OffsetDateTime.now();
		final var document = new Document();
		final var signedDocument = new Document();
		final var additionalDocuments = List.of(new Document());
		final var signingId = "signingId";

		// Act
		final var result = SigningInstance.builder()
			.withExpires(expires)
			.withCustomerReference(customerReference)
			.withStatus(status)
			.withSignatories(signatories)
			.withInitiator(initiator)
			.withNotificationMessages(notificationMessage)
			.withAdditionalParties(additionalParties)
			.withDocument(document)
			.withAdditionalDocuments(additionalDocuments)
			.withCreated(created)
			.withChanged(changed)
			.withSigningId(signingId)
			.withSignedDocument(signedDocument)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getExpires()).isEqualTo(expires);
		assertThat(result.getCustomerReference()).isEqualTo(customerReference);
		assertThat(result.getStatus()).isEqualTo(status);
		assertThat(result.getSignatories()).isEqualTo(signatories);
		assertThat(result.getInitiator()).isEqualTo(initiator);
		assertThat(result.getNotificationMessages()).isEqualTo(notificationMessage);
		assertThat(result.getAdditionalParties()).isEqualTo(additionalParties);
		assertThat(result.getDocument()).isEqualTo(document);
		assertThat(result.getAdditionalDocuments()).isEqualTo(additionalDocuments);
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getChanged()).isEqualTo(changed);
		assertThat(result.getSigningId()).isEqualTo(signingId);
		assertThat(result.getSignedDocument()).isEqualTo(signedDocument);

	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(SigningInstance.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SigningInstance()).hasAllNullFieldsOrProperties();
	}

}
