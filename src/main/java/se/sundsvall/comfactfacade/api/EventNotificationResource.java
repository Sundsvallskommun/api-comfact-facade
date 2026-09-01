package se.sundsvall.comfactfacade.api;

import generated.se.sundsvall.comfact.EventNotification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.comfactfacade.service.EventNotificationService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/webhooks")
@Tag(name = "Webhooks", description = "Inbound provider event webhooks")

@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class EventNotificationResource {

	private final EventNotificationService eventNotificationService;

	EventNotificationResource(final EventNotificationService eventNotificationService) {
		this.eventNotificationService = eventNotificationService;
	}

	@PostMapping(path = "/comfact", consumes = APPLICATION_JSON_VALUE)
	@Operation(summary = "Receive a Comfact signing event.",
		description = "Called by Comfact when a signing instance event occurs, not by other Sundsvall services. The event is forwarded, unchanged, to api-service-e-signing.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation")
	})
	ResponseEntity<Void> handleComfactEvent(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@RequestBody final EventNotification notification) {
		eventNotificationService.handleComfactEvent(municipalityId, notification);
		return ok().build();
	}
}
