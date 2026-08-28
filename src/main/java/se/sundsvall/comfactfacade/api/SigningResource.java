package se.sundsvall.comfactfacade.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.api.model.UpdateSigningRequest;
import se.sundsvall.comfactfacade.service.SigningService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static se.sundsvall.comfactfacade.api.ApiConstants.ACCOUNT_KEY_HEADER;

@RestController
@Validated
@RequestMapping("/{municipalityId}/signings")
@Tag(name = "Signings", description = "Signing operations")

@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class SigningResource {

	// The x-account-key header parameters below are consumed by ComfactAccountInterceptor (which resolves the
	// Comfact account for the request) - they are declared here for OpenAPI documentation.

	private final SigningService signingService;

	SigningResource(final SigningService signingService) {
		this.signingService = signingService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all signing instances.",
		description = "The 'sort' parameter in the Pageable object can take the values 'SigningInstanceId', 'ReferenceNumber', 'CustomerReferenceNumber', 'Created', 'Changed', 'Expires', 'StatusCode', 'StatusMessage', 'UserId', 'Language', 'SignatoryReminderStartDate', 'InitiatorEmailAddress', 'QueueCreated', 'QueueChanged'. Will default to 'SigningInstanceId' if not set. Will default to page 0 with a size of 10 if not set.")

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<SigningsResponse> getSigningRequests(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@RequestHeader(name = ACCOUNT_KEY_HEADER, required = false) @Parameter(name = ACCOUNT_KEY_HEADER,
			description = "Key selecting which Comfact account to use. When omitted, the municipality's default account is used.",
			example = "social-services") final String accountKey,
		final Pageable pageable) {

		return ok(signingService.getSigningRequests(pageable));
	}

	@GetMapping(path = "{signingId}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get a signing request.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<SigningInstance> getSigningRequest(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@RequestHeader(name = ACCOUNT_KEY_HEADER, required = false) @Parameter(name = ACCOUNT_KEY_HEADER,
			description = "Key selecting which Comfact account to use. When omitted, the municipality's default account is used.",
			example = "social-services") final String accountKey,
		@PathVariable final String signingId) {

		return ok(signingService.getSigningRequest(signingId));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Create Signing instance.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<CreateSigningResponse> createSigningRequest(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@RequestHeader(name = ACCOUNT_KEY_HEADER, required = false) @Parameter(name = ACCOUNT_KEY_HEADER,
			description = "Key selecting which Comfact account to use. When omitted, the municipality's default account is used.",
			example = "social-services") final String accountKey,
		@Valid @RequestBody final SigningRequest signingRequest) {

		return ok(signingService.createSigningRequest(municipalityId, signingRequest));
	}

	@PatchMapping(path = "{signingId}", consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(summary = "Update a signing instance.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
		@ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> updateSigningRequest(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@RequestHeader(name = ACCOUNT_KEY_HEADER, required = false) @Parameter(name = ACCOUNT_KEY_HEADER,
			description = "Key selecting which Comfact account to use. When omitted, the municipality's default account is used.",
			example = "social-services") final String accountKey,
		@PathVariable final String signingId,
		@Valid @RequestBody final UpdateSigningRequest updateSigningRequest) {

		signingService.updateSigningRequest(signingId, updateSigningRequest);
		return noContent()
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@GetMapping(path = "{signingId}/signatory/{partyId}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get information about the current signatory")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Signatory> getSignatory(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@RequestHeader(name = ACCOUNT_KEY_HEADER, required = false) @Parameter(name = ACCOUNT_KEY_HEADER,
			description = "Key selecting which Comfact account to use. When omitted, the municipality's default account is used.",
			example = "social-services") final String accountKey,
		@PathVariable final String signingId,
		@PathVariable final String partyId) {

		return ok(signingService.getSignatory(signingId, partyId));
	}
}
