package se.sundsvall.comfactfacade.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.dept44.common.validators.annotation.ValidBase64;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Document {

	@Schema(description = "The Document Name visible to the user in the signing process.", nullable = true, example = "Business Contract")
	private String name;

	@NotBlank
	@Schema(description = "The document file name including extension,", example = "document.pdf")
	private String fileName;

	@OneOf({
		"application/pdf"
	})
	@Schema(description = "The documents mimetype. Must be application/pdf", example = "application/pdf")
	private String mimeType;

	@ValidBase64
	@Schema(type = "string", format = "byte", description = "Base64-encoded file (plain text)", example = "dGVzdA==")
	private String content;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Document document = (Document) o;
		return Objects.equals(name, document.name) && Objects.equals(fileName, document.fileName) && Objects.equals(mimeType, document.mimeType) && Objects.equals(content, document.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, fileName, mimeType, content);
	}

	@Override
	public String toString() {
		return "Document{" +
			"name='" + name + '\'' +
			", fileName='" + fileName + '\'' +
			", mimeType='" + mimeType + '\'' +
			", content='" + content + '\'' +
			'}';
	}

}
