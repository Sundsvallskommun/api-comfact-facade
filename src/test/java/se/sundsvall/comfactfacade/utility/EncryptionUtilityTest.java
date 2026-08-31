package se.sundsvall.comfactfacade.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.comfactfacade.configuration.CredentialsProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptionUtilityTest {

	@Mock
	private CredentialsProperties credentialsPropertiesMock;

	@InjectMocks
	private EncryptionUtility encryptionUtility;

	@Test
	void encryptAndDecrypt() {
		final var input = "someInput";

		when(credentialsPropertiesMock.secretKey()).thenReturn("WbVG8XC%m&9Z!7a$xyKGWzB^#kUSoUUs");

		final var encodedResult = encryptionUtility.encrypt(input.getBytes());
		final var decodedResult = encryptionUtility.decrypt(encodedResult);

		assertThat(encodedResult).isNotEqualTo(input);
		assertThat(decodedResult).isEqualTo(input);
	}

	@Test
	void encryptProducesUniqueCipherTexts() {
		final var input = "someInput";

		when(credentialsPropertiesMock.secretKey()).thenReturn("WbVG8XC%m&9Z!7a$xyKGWzB^#kUSoUUs");

		assertThat(encryptionUtility.encrypt(input.getBytes()))
			.isNotEqualTo(encryptionUtility.encrypt(input.getBytes()));
	}

	@Test
	void encryptWithInvalidKey() {
		when(credentialsPropertiesMock.secretKey()).thenReturn("too-short");

		assertThatThrownBy(() -> encryptionUtility.encrypt("someInput".getBytes()))
			.isInstanceOf(EncryptionException.class)
			.hasMessage("Something went wrong encrypting input");
	}

	@Test
	void decryptWithWrongKey() {
		when(credentialsPropertiesMock.secretKey()).thenReturn("WbVG8XC%m&9Z!7a$xyKGWzB^#kUSoUUs");
		final var encodedResult = encryptionUtility.encrypt("someInput".getBytes());

		when(credentialsPropertiesMock.secretKey()).thenReturn("AnotherKeyAnotherKeyAnotherKey12");

		assertThatThrownBy(() -> encryptionUtility.decrypt(encodedResult))
			.isInstanceOf(EncryptionException.class)
			.hasMessage("Something went wrong decrypting input");
	}
}
