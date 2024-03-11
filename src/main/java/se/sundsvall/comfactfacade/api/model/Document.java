package se.sundsvall.comfactfacade.api.model;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record Document(
	@Schema(description = "The Document Name visible to the user in the signing process.", nullable = true, example = "Business Contract")
	String name,
	@NotBlank @Schema(description = "The document file name including extension,", example = "document.pdf")
	String fileName,
	@NotBlank @Schema(description = "The documents mimetype. Must be application/pdf", example = "application/pdf")
	String mimeType,
	@NotBlank @Schema(type = "string", format = "byte", description = "Base64-encoded file (plain text)", example = "dGVzdA==")
	String content
) {

}
