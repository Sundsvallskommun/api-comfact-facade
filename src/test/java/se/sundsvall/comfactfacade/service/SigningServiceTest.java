package se.sundsvall.comfactfacade.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.comfactfacade.TestUtil;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {

	@InjectMocks
	private SigningService signingService;

	@Test
	void createSigningRequest() {

		final var result = signingService.createSigningRequest(new TestUtil.SigningRequestBuilder().build());
		assertThat(result).isNotNull().isEqualTo("OK");
	}

	@Test
	void cancelSigningRequest() {

		// Arrange
		final var signingId = "someSigningId";
		// Act
		final var result = signingService.cancelSigningRequest(signingId);
		// Assert
		assertThat(result).isNotNull().isEqualTo("OK");
	}

	@Test
	void getStatus() {
		// Arrange
		final var signingId = "someSigningId";
		// Act
		final var result = signingService.getStatus(signingId);
		// Assert
		assertThat(result).isNotNull().isEqualTo("OK");
	}

	@Test
	void getSignedDocument() {
		// Arrange
		final var signingId = "someSigningId";
		// Act
		final var result = signingService.getSignedDocument(signingId);
		// Assert
		assertThat(result).isNotNull();
	}

}
