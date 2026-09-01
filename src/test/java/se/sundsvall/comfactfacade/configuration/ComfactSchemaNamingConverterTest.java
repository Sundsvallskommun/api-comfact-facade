package se.sundsvall.comfactfacade.configuration;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComfactSchemaNamingConverterTest {

	@Mock
	private ModelConverterContext contextMock;

	@Mock
	private ModelConverter nextConverterMock;

	private final ComfactSchemaNamingConverter converter = new ComfactSchemaNamingConverter();

	@Test
	void renamesGeneratedComfactSchema() {
		// Arrange
		final var model = new ObjectSchema().name("Document");
		final var type = new AnnotatedType(generated.se.sundsvall.comfact.Document.class);
		when(nextConverterMock.resolve(any(), any(), any())).thenReturn(model);
		when(contextMock.getDefinedModels()).thenReturn(Map.of("Document", model));

		// Act
		final var result = converter.resolve(type, contextMock, List.of(nextConverterMock).iterator());

		// Assert
		assertThat(result.getName()).isEqualTo("ComfactDocument");
		verify(contextMock).defineModel("ComfactDocument", model, type, "Document");
	}

	@Test
	void rewritesReferenceToRenamedSchema() {
		// Arrange
		final var reference = new Schema<>().$ref("Document");
		final var type = new AnnotatedType(generated.se.sundsvall.comfact.Document.class);
		when(nextConverterMock.resolve(any(), any(), any())).thenReturn(reference);
		when(contextMock.getDefinedModels()).thenReturn(Map.of());

		// Act
		final var result = converter.resolve(type, contextMock, List.of(nextConverterMock).iterator());

		// Assert
		assertThat(result.get$ref()).isEqualTo("#/components/schemas/ComfactDocument");
	}

	@Test
	void leavesOurOwnSchemasAlone() {
		// Arrange
		final var model = new ObjectSchema().name("Document");
		final var type = new AnnotatedType(se.sundsvall.comfactfacade.api.model.Document.class);
		when(nextConverterMock.resolve(any(), any(), any())).thenReturn(model);

		// Act
		final var result = converter.resolve(type, contextMock, List.of(nextConverterMock).iterator());

		// Assert
		assertThat(result.getName()).isEqualTo("Document");
		verify(contextMock, never()).defineModel(anyString(), any(Schema.class), any(AnnotatedType.class), anyString());
	}

	@Test
	void returnsNullAtEndOfChain() {
		// Act
		final var result = converter.resolve(new AnnotatedType(generated.se.sundsvall.comfact.Document.class), contextMock, List.<ModelConverter>of().iterator());

		// Assert
		assertThat(result).isNull();
		verifyNoInteractions(contextMock);
	}
}
