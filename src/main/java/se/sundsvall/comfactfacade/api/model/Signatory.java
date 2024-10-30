package se.sundsvall.comfactfacade.api.model;

import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(setterPrefix = "with")
@Schema(description = "A Signatory related to the signing process.")
public class Signatory extends Party {

	@NotEmpty
	@ArraySchema(schema = @Schema(implementation = Identification.class))
	private List<Identification> identifications;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final Signatory signatory = (Signatory) o;
		return Objects.equals(identifications, signatory.identifications);
	}

	@Override
	public String toString() {
		return "Signatory{" +
			"super=" + super.toString() +
			", identifications=" + identifications +
			'}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), identifications);
	}

}
