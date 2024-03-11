package se.sundsvall.comfactfacade.api.model;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Custom message for the signature request emails.")
public record NotificationMessage(

	@Schema(description = "The subject of the notification message", example = "Please sign the document")
	String subject,

	@Schema(description = "The body of the notification message", example = "Dear John Doe, please sign the document.")
	String body,

	@NotBlank
	@Schema(description = "The language of the notification message.", example = "sv")
	String language) {

}
