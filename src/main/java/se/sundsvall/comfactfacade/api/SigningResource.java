package se.sundsvall.comfactfacade.api;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.service.SigningService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/{municipalityId}/signings")
@Tag(name = "Signings", description = "Signing operations")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class SigningResource {

	private final SigningService signingService;

	public SigningResource(final SigningService signingService) {this.signingService = signingService;}

	@GetMapping(produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get all signing instances.", description = "The 'sort' parameter in the Pageable object can take the values 'SigningInstanceId', 'ReferenceNumber', 'CustomerReferenceNumber', 'Created', 'Changed', 'Expires', 'StatusCode', 'StatusMessage', 'UserId', 'Language', 'SignatoryReminderStartDate', 'InitiatorEmailAddress', 'QueueCreated', 'QueueChanged'. Will default to 'SigningInstanceId' if not set. Will default to page 0 with a size of 10 if not set.")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<SigningsResponse> getSigningRequests(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281")
		@PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		final Pageable pageable) {
		return ResponseEntity.ok(signingService.getSigningRequests(municipalityId, pageable));
	}

	@GetMapping(path = "{signingId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get a signing request.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<SigningInstance> getSigningRequest(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281")
		@PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@PathVariable final String signingId) {
		return ResponseEntity.ok(signingService.getSigningRequest(municipalityId, signingId));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Create Signing instance.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<CreateSigningResponse> createSigningRequest(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281")
		@PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Valid @RequestBody final SigningRequest signingRequest) {

		return ResponseEntity.ok(signingService.createSigningRequest(municipalityId, signingRequest));
	}

	@PatchMapping(path = "{signingId}", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Update a signing instance.")
	@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> updateSigningRequest(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281")
		@PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@PathVariable final String signingId,
		@Valid @RequestBody final SigningRequest signingRequest) {
		signingService.updateSigningRequest(municipalityId, signingId, signingRequest);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "{signingId}", produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Annul a signing instance.")
	@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> cancelSigningRequest(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281")
		@PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@PathVariable final String signingId) {
		signingService.cancelSigningRequest(municipalityId, signingId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "{signingId}/signatory/{partyId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get information about the current signatory")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Signatory> getSignatory(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281")
		@PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@PathVariable final String signingId,
		@PathVariable final String partyId) {
		final var result = signingService.getSignatory(municipalityId, signingId, partyId);
		return ResponseEntity.ok(result);
	}


}
