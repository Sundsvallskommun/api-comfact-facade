package se.sundsvall.comfactfacade.api;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.TestUtil;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.service.SigningService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SigningResourceTest {

	@MockBean
	private SigningService signingServiceMock;

	@Autowired
	private WebTestClient webTestClient;


	@Test
	void createSigningRequest() {
		// Arrange
		final var signingRequest = new TestUtil.SigningRequestBuilder().build();
		// Act
		webTestClient.post()
			.uri("/signing")
			.contentType(APPLICATION_JSON)
			.bodyValue(signingRequest)
			.exchange()
			.expectStatus().isOk();

		// Assert
		verify(signingServiceMock).createSigningRequest(any(SigningRequest.class));
	}

	@Test
	void cancelSigningRequest() {
		// Arrange
		final String signingId = "someSigningId";
		doNothing().when(signingServiceMock).cancelSigningRequest(signingId);

		// Act
		webTestClient.delete()
			.uri("/signing/{signingId}", signingId)
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(signingServiceMock).cancelSigningRequest(signingId);
	}

	@Test
	void cancelSigningRequest_NotFound() {
		// Arrange
		final String signingId = "someSigningId";
		doThrow(Problem.valueOf(Status.NOT_FOUND, "The signing request with id someSigningId was not found")).when(signingServiceMock).cancelSigningRequest(signingId);

		// Act
		final var result = webTestClient.delete()
			.uri("/signing/{signingId}", signingId)
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
	void getStatus() {
		// Arrange
		final String signingId = "someSigningId";
		when(signingServiceMock.getStatus(signingId)).thenReturn("OK");

		// Act & Assert
		webTestClient.get()
			.uri("/signing/{signingId}", signingId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.isEqualTo("OK");


		verify(signingServiceMock).getStatus(signingId);
	}


	@Test
	void getStatus_NotFound() {
		// Arrange
		final String signingId = "someSigningId";
		doThrow(Problem.valueOf(Status.NOT_FOUND, "The signing request with id someSigningId was not found")).when(signingServiceMock).getStatus(signingId);

		// Act & Assert
		final var result = webTestClient.get()
			.uri("/signing/{signingId}", signingId)
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
		verify(signingServiceMock).getStatus(signingId);
	}

	@Test
	void getSignedDocument() {
		// Arrange
		final var name = "someName";
		final var fileName = "someFileName";
		final var mimeType = "someContentType";
		final var content = "someContent";
		final var signingId = "someSigningId";

		// Act
		final var document = new Document(name, fileName, mimeType, content);
		when(signingServiceMock.getSignedDocument(any(String.class))).thenReturn(document);

		// Act & Assert
		webTestClient.get()
			.uri("/signing/{signingId}/document", signingId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(Document.class)
			.isEqualTo(document);

		verify(signingServiceMock).getSignedDocument(signingId);
	}

	@Test
	void getSignedDocument_NotFound() {
		// Arrange
		final var signingId = "someSigningId";
		doThrow(Problem.valueOf(Status.NOT_FOUND, "The document with id someSigningId was not found")).when(signingServiceMock).getSignedDocument(any(String.class));

		// Act & Assert
		final var result = webTestClient.get()
			.uri("/signing/{signingId}/document", signingId)
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
		assertThat(result.getDetail()).isEqualTo("The document with id someSigningId was not found");
		verify(signingServiceMock).getSignedDocument(signingId);
	}

}
