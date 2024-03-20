package se.sundsvall.comfactfacade.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.service.SigningService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceFailureTest {

	@MockBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void cancelSigningRequest_NotFound() {
		// Arrange
		final String signingId = "someSigningId";
		doThrow(Problem.valueOf(Status.NOT_FOUND, "The signing request with id someSigningId was not found")).when(signingServiceMock).cancelSigningRequest(signingId);

		// Act
		final var result = webTestClient.delete()
			.uri("/signings/{signingId}", signingId)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(result.getTitle()).isEqualTo("Not Found");
		assertThat(result.getDetail()).isEqualTo("The signing request with id someSigningId was not found");
		verify(signingServiceMock).cancelSigningRequest(signingId);
	}

	@Test
	void createSigningRequest_withoutInitiator() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder()
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("someMimeType")
				.withContent("someContent")
				.build())
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
			assertThat(violations).hasSize(1);
			assertThat(violations.getFirst().getField()).isEqualTo("initiator");
			assertThat(violations.getFirst().getMessage()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}


	@Test
	void createSigningRequest_withoutSignatory() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withInitiator(Party.builder()
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("someMimeType")
				.withContent("someContent")
				.build())
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
			assertThat(violations).hasSize(1);
			assertThat(violations.getFirst().getField()).isEqualTo("signatory");
			assertThat(violations.getFirst().getMessage()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withoutDocument() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder()
				.build())
			.withInitiator(Party.builder()
				.build())
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
			assertThat(violations).hasSize(1);
			assertThat(violations.getFirst().getField()).isEqualTo("document");
			assertThat(violations.getFirst().getMessage()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}


	@Test
	void createSigningRequest_withFaultyPersonalNumber() {

		// Arrange
		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder()
				.withPersonalNumber("not a valid personal number")
				.build())
			.withInitiator(Party.builder()
				.withPersonalNumber("not a valid personal number")
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("someMimeType")
				.withContent("someContent")
				.build())
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
			assertThat(violations).hasSize(2);
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("signatory.personalNumber", "initiator.personalNumber");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("must match the regular expression ^(19|20)[0-9]{10}$"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyPartyId() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder()
				.withPartyId("not a valid UUID")
				.build())
			.withInitiator(Party.builder()
				.withPartyId("not a valid UUID")
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("someMimeType")
				.withContent("someContent")
				.build())
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
			assertThat(violations).hasSize(2);
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("signatory.partyId", "initiator.partyId");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("not a valid UUID"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyPhoneNumber() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder()
				.withPhoneNumber("not a valid phone number")
				.build())
			.withInitiator(Party.builder()
				.withPhoneNumber("not a valid phone number")
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("someMimeType")
				.withContent("someContent")
				.build())
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
			assertThat(violations).hasSize(2);
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("signatory.phoneNumber", "initiator.phoneNumber");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("must match the regular expression ^07[02369]\\d{7}$"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyDocument() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatory(Party.builder()
				.build())
			.withInitiator(Party.builder()
				.build())
			.withDocument(Document.builder()
				.build())
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
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("document.fileName", "document.content", "document.mimeType");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("must not be blank"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void getSignatory_NotFound() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		doThrow(Problem.valueOf(Status.NOT_FOUND, "The signing request with id someSigningId was not found"))
			.when(signingServiceMock).getSignatory(signingId, partyId);

		// Act & Assert
		final var result = webTestClient.get()
			.uri("/signings/{signingId}/signatory/{partyId}", signingId, partyId)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(result.getTitle()).isEqualTo("Not Found");
		assertThat(result.getDetail()).isEqualTo("The signing request with id someSigningId was not found");
		verify(signingServiceMock).getSignatory(signingId, partyId);
	}


}
