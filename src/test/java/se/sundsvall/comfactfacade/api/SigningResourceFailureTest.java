package se.sundsvall.comfactfacade.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Identification;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.service.SigningService;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class SigningResourceFailureTest {

	@MockitoBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void cancelSigningRequest_NotFound() {
		// Arrange
		final String signingId = "someSigningId";
		doThrow(Problem.valueOf(HttpStatus.NOT_FOUND, "The signing request with id someSigningId was not found")).when(signingServiceMock).cancelSigningRequest(MUNICIPALITY_ID, signingId);

		// Act
		final var result = webTestClient.delete()
			.uri("/{municipalityId}/signings/{signingId}", MUNICIPALITY_ID, signingId)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(result.getTitle()).isEqualTo("Not Found");
		assertThat(result.getDetail()).isEqualTo("The signing request with id someSigningId was not found");
		verify(signingServiceMock).cancelSigningRequest(MUNICIPALITY_ID, signingId);
	}

	@Test
	void createSigningRequest_withoutInitiator() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder()
				.withEmail("someEmail")
				.withIdentifications(List.of(Identification.builder().withAlias("SvensktEId").build()))
				.build()))
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("application/pdf")
				.withContent("someContent")
				.build())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/signings", MUNICIPALITY_ID)
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
			assertThat(violations.getFirst().field()).isEqualTo("initiator");
			assertThat(violations.getFirst().message()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withoutSignatory() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withInitiator(Signatory.builder()
				.withEmail("someEmail")
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("application/pdf")
				.withContent("someContent")
				.build())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/signings", MUNICIPALITY_ID)
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
			assertThat(violations.getFirst().field()).isEqualTo("signatories");
			assertThat(violations.getFirst().message()).isEqualTo("must not be empty");
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withoutDocument() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder()
				.withEmail("someEmail")
				.withIdentifications(List.of(Identification.builder().withAlias("SvensktEId").build()))
				.build()))
			.withInitiator(Party.builder()
				.withEmail("someEmail")
				.build())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/signings", MUNICIPALITY_ID)
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
			assertThat(violations.getFirst().field()).isEqualTo("document");
			assertThat(violations.getFirst().message()).isEqualTo("must not be null");
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyPartyId() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder()
				.withEmail("someEmail")
				.withIdentifications(List.of(Identification.builder().withAlias("SvensktEId").build()))
				.withPartyId("not a valid UUID")
				.build()))
			.withInitiator(Party.builder()
				.withEmail("someEmail")
				.withPartyId("not a valid UUID")
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("application/pdf")
				.withContent("someContent")
				.build())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/signings", MUNICIPALITY_ID)
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
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("signatories[0].partyId", "initiator.partyId");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("not a valid UUID"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyPhoneNumber() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder()
				.withEmail("someEmail")
				.withIdentifications(List.of(Identification.builder().withAlias("SvensktEId").build()))
				.withPhoneNumber("not a valid phone number")
				.build()))
			.withInitiator(Party.builder()
				.withEmail("someEmail")
				.withPhoneNumber("not a valid phone number")
				.build())
			.withDocument(Document.builder()
				.withFileName("someFileName")
				.withMimeType("application/pdf")
				.withContent("someContent")
				.build())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/signings", MUNICIPALITY_ID)
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
			assertThat(violations).extracting("field").containsExactlyInAnyOrder("signatories[0].phoneNumber", "initiator.phoneNumber");
			assertThat(violations).extracting("message").allMatch(message -> message.equals("must match the regular expression ^07[02369]\\d{7}$"));
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void createSigningRequest_withFaultyDocument() {

		// Arrange
		final var signingRequest = SigningRequest.builder()
			.withSignatories(List.of(Signatory.builder()
				.withEmail("someEmail")
				.withIdentifications(List.of(Identification.builder().withAlias("SvensktEId").build()))
				.build()))
			.withInitiator(Party.builder()
				.withEmail("someEmail")
				.build())
			.withDocument(Document.builder()
				.build())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/signings", MUNICIPALITY_ID)
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
			assertThat(violations).extracting("message").containsExactlyInAnyOrder("not a valid BASE64-encoded string", "must not be blank", "must be one of: [application/pdf]");
		});

		verifyNoInteractions(signingServiceMock);
	}

	@Test
	void getSignatory_NotFound() {
		// Arrange
		final var signingId = "someSigningId";
		final var partyId = "somePartyId";
		doThrow(Problem.valueOf(HttpStatus.NOT_FOUND, "The signing request with id someSigningId was not found"))
			.when(signingServiceMock).getSignatory(MUNICIPALITY_ID, signingId, partyId);

		// Act & Assert
		final var result = webTestClient.get()
			.uri("/{municipalityId}/signings/{signingId}/signatory/{partyId}", MUNICIPALITY_ID, signingId, partyId)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(result.getTitle()).isEqualTo("Not Found");
		assertThat(result.getDetail()).isEqualTo("The signing request with id someSigningId was not found");
		verify(signingServiceMock).getSignatory(MUNICIPALITY_ID, signingId, partyId);
	}

}
