package se.sundsvall.comfactfacade.configuration;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Iterator;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Prefixes the schema names of the models generated from Comfacts contract when they appear in this service's
 * OpenAPI specification.
 */
@Component
class ComfactSchemaNamingConverter implements ModelConverter {

	private static final String GENERATED_MODEL_PACKAGE = "generated.se.sundsvall.comfact.";

	private static final String SCHEMA_NAME_PREFIX = "Comfact";

	@Override
	public Schema<?> resolve(final AnnotatedType type, final ModelConverterContext context, final Iterator<ModelConverter> chain) {
		if (!chain.hasNext()) {
			return null;
		}

		final var originalName = generatedComfactSchemaName(type).orElse(null);
		final var resolved = chain.next().resolve(type, context, chain);
		if ((originalName == null) || (resolved == null)) {
			return resolved;
		}

		final var prefixedName = SCHEMA_NAME_PREFIX + originalName;

		// Re-register the model under the prefixed name, dropping the entry under Comfact's own name.
		Optional.ofNullable(context.getDefinedModels().get(originalName))
			.ifPresent(model -> {
				model.setName(prefixedName);
				context.defineModel(prefixedName, model, type, originalName);
			});

		// Point whatever the chain handed back at the renamed schema.
		if (resolved.get$ref() != null) {
			return new Schema<>().$ref(prefixedName);
		}
		resolved.setName(prefixedName);

		return resolved;
	}

	private Optional<String> generatedComfactSchemaName(final AnnotatedType type) {
		if (type.getType() == null) {
			return Optional.empty();
		}

		final var rawClass = Json.mapper().constructType(type.getType()).getRawClass();
		if (!rawClass.getName().startsWith(GENERATED_MODEL_PACKAGE)) {
			return Optional.empty();
		}

		return Optional.ofNullable(rawClass.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class))
			.map(io.swagger.v3.oas.annotations.media.Schema::name)
			.filter(name -> !name.isBlank())
			.or(() -> Optional.of(rawClass.getSimpleName()));
	}
}
