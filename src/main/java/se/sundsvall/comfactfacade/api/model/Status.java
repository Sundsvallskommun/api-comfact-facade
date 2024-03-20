package se.sundsvall.comfactfacade.api.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Status {

	@Schema(description = "The status code", example = "Created")
	private String code;

	@Schema(description = "The status message", example = "The signing instance is halted.")
	private String message;

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Status status = (Status) o;
		return Objects.equals(code, status.code) && Objects.equals(message, status.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, message);
	}

	@Override
	public String toString() {
		return "Status{" +
			"code='" + code + '\'' +
			", message='" + message + '\'' +
			'}';
	}


}
