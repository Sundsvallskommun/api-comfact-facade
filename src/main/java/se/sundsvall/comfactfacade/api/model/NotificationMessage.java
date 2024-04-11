package se.sundsvall.comfactfacade.api.model;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;

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
@Schema(description = "Custom message for the signature request emails.")
public class NotificationMessage {

	@Schema(description = "The subject of the notification message", example = "Please sign the document")
	private String subject;

	@Schema(description = "The body of the notification message", example = "Dear John Doe, please sign the document.")
	private String body;

	@NotBlank
	@Schema(description = "The language of the notification message.", example = "sv-SE")
	private String language;

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final NotificationMessage that = (NotificationMessage) o;
		return Objects.equals(subject, that.subject) && Objects.equals(body, that.body) && Objects.equals(language, that.language);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subject, body, language);
	}

	@Override
	public String toString() {
		return "NotificationMessage{" +
			"subject='" + subject + '\'' +
			", body='" + body + '\'' +
			", language='" + language + '\'' +
			'}';
	}

}
