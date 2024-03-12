package se.sundsvall.comfactfacade.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.TestUtil;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.service.SigningService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceFailureTest {

	@MockBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createSigningRequest_withoutInitiator() {

		// Arrange
		final var signingRequest = new TestUtil.SigningRequestBuilder().withInitiator(null).build();

		// Act
		final var response = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations()).satisfies(violations -> {
			assertThat(violations).hasSize(1);
			assertThat(violations.getFirst().getField()).isEqualTo("initiator");
			assertThat(violations.getFirst().getMessage()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}


	@Test
	void createSigningRequest_withoutSignatory() {

		// Arrange
		final var signingRequest = new TestUtil.SigningRequestBuilder().withSignatory(null).build();

		// Act
		final var response = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations()).satisfies(violations -> {
			assertThat(violations).hasSize(1);
			assertThat(violations.getFirst().getField()).isEqualTo("signatory");
			assertThat(violations.getFirst().getMessage()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withoutDocument() {

		// Arrange
		final var signingRequest = new TestUtil.SigningRequestBuilder().withDocument(null).build();

		// Act
		final var response = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations()).satisfies(violations -> {
			assertThat(violations).hasSize(1);
			assertThat(violations.getFirst().getField()).isEqualTo("document");
			assertThat(violations.getFirst().getMessage()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}


	@Test
	void createSigningRequest_withFaultyPersonalNumber() {

		// Arrange
		final var customParty = new TestUtil.SigningRequestBuilder().withPartyProp("personalNumber", "faultyPersonalNumber").defaultParty;
		final var signingRequest = new TestUtil.SigningRequestBuilder()
			.withInitiator(customParty)
			.withAdditionalParty(customParty)
			.withSignatory(customParty)
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations()).satisfies(violations -> {
			assertThat(violations).hasSize(3);
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("additionalParty.personalNumber", "signatory.personalNumber", "initiator.personalNumber");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("must match the regular expression ^(19|20)[0-9]{10}$"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyPartyId() {

		// Arrange
		final var customParty = new TestUtil.SigningRequestBuilder().withPartyProp("partyId", "faultyPartyId").defaultParty;
		final var signingRequest = new TestUtil.SigningRequestBuilder()
			.withInitiator(customParty)
			.withAdditionalParty(customParty)
			.withSignatory(customParty)
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations()).satisfies(violations -> {
			assertThat(violations).hasSize(3);
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("additionalParty.partyId", "signatory.partyId", "initiator.partyId");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("not a valid UUID"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyPhoneNumber() {

		// Arrange
		final var customParty = new TestUtil.SigningRequestBuilder().withPartyProp("phoneNumber", "faultyPhoneNumber").defaultParty;
		final var signingRequest = new TestUtil.SigningRequestBuilder()
			.withInitiator(customParty)
			.withAdditionalParty(customParty)
			.withSignatory(customParty)
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations()).satisfies(violations -> {
			assertThat(violations).hasSize(3);
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("additionalParty.phoneNumber", "signatory.phoneNumber", "initiator.phoneNumber");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("must match the regular expression ^07[02369]\\d{7}$"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyDocument() {

		// Arrange
		final var signingRequest = new TestUtil.SigningRequestBuilder().withDocument(new Document(null, null, null, null)).build();
		// Act
		final var response = webTestClient.post()
			.uri("/signings")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations()).satisfies(violations -> {
			assertThat(violations).hasSize(3);
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("document.fileName", "document.content", "document.mimeType");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("must not be blank"));
		});

		verifyNoInteractions(signingServiceMock);
	}


}
