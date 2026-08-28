package se.sundsvall.comfactfacade.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.util.UriComponentsBuilder;
import se.sundsvall.comfactfacade.api.model.Account;
import se.sundsvall.comfactfacade.service.AccountService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/accounts")
@Tag(name = "Accounts", description = "Comfact account configuration mappings")

@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class AccountResource {

	private final AccountService accountService;

	AccountResource(final AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all account mappings for the municipality.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<List<Account>> getAccounts(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId) {

		return ok(accountService.getAccounts(municipalityId));
	}

	@GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get an account mapping.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Account> getAccount(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@PathVariable final String id) {

		return ok(accountService.getAccount(municipalityId, id));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(summary = "Create an account mapping.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Successful operation", headers = @Header(name = "Location", description = "Location of the created account"), useReturnTypeSchema = true),
		@ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> createAccount(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@Valid @RequestBody final Account account) {

		final var id = accountService.createAccount(municipalityId, account);
		return created(UriComponentsBuilder.fromPath("/{municipalityId}/accounts/{id}").buildAndExpand(municipalityId, id).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@PatchMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(summary = "Update an account mapping. Null fields are left unchanged.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> updateAccount(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@PathVariable final String id,
		@RequestBody final Account account) {

		accountService.updateAccount(municipalityId, id, account);
		return noContent()
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@DeleteMapping(path = "{id}", produces = ALL_VALUE)
	@Operation(summary = "Delete an account mapping.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> deleteAccount(
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId final String municipalityId,
		@PathVariable final String id) {

		accountService.deleteAccount(municipalityId, id);
		return noContent()
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}
}
