package se.sundsvall.comfactfacade.api;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.List;

import jakarta.validation.Valid;

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

import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
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

	@GetMapping(produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get all signing instances.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<List<SigningInstance>> getSigningRequests() {
		return ResponseEntity.ok(signingService.getSigningRequests());
	}

	@GetMapping(path = "{signingId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get a signing request.")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<SigningInstance> getSigningRequest(@PathVariable final String signingId) {
		return ResponseEntity.ok(signingService.getSigningRequest(signingId));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Create Signing instance.")
	@ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true)
	public ResponseEntity<String> createSigningRequest(@Valid @RequestBody final SigningRequest signingRequest) {
		
		return ResponseEntity.created(
				fromPath("/signings/{signingId}")
					.buildAndExpand(signingService.createSigningRequest(signingRequest))
					.toUri())
			.build();
	}

	@PatchMapping(path = "{signingId}", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Update a signing instance.")
	@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> updateSigningRequest(@PathVariable final String signingId, @Valid @RequestBody final SigningRequest signingRequest) {
		signingService.updateSigningRequest(signingId, signingRequest);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "{signingId}", produces = {APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Annul a signing instance.")
	@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> cancelSigningRequest(@PathVariable final String signingId) {
		signingService.cancelSigningRequest(signingId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "{signingId}/signatory/{partyId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Get information about the current signatory")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Party> getSignatory(@PathVariable final String signingId, @PathVariable final String partyId) {
		final var result = signingService.getSignatory(signingId, partyId);
		return ResponseEntity.ok(result);
	}


}
