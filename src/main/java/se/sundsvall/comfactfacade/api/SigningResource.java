package se.sundsvall.comfactfacade.api;


import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.service.SigningService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/signings")
@Tag(name = "Signings", description = "Signing operations")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
public class SigningResource {

	private final SigningService signingService;

	public SigningResource(final SigningService signingService) {this.signingService = signingService;}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Create Signing request.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<Void> createSigningRequest(@Valid @RequestBody final SigningRequest signingRequest) {
		signingService.createSigningRequest(signingRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(path = "{signingId}", produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Annul a signing request.")
	@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> cancelSigningRequest(@PathVariable final String signingId) {
		signingService.cancelSigningRequest(signingId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "{signingId}", produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get status of a signing request.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<String> getStatus(@PathVariable final String signingId) {
		return ResponseEntity.ok(signingService.getStatus(signingId));
	}


	@GetMapping(path = "{signingId}/document", produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get signed document.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Document> getSignedDocument(@PathVariable final String signingId) {
		return ResponseEntity.ok(signingService.getSignedDocument(signingId));
	}


}
