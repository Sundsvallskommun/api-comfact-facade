package se.sundsvall.comfactfacade.integration.comfact;

import generated.se.sundsvall.comfact.SearchFilter;
import generated.se.sundsvall.comfact.SearchResult;
import generated.se.sundsvall.comfact.Signatory;
import generated.se.sundsvall.comfact.SigningInstance;
import generated.se.sundsvall.comfact.SigningInstanceInput;
import generated.se.sundsvall.comfact.SigningInstancePatch;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.comfact.url}/api/v3", configuration = ComfactConfiguration.class)
public interface ComfactClient {

	/**
	 * Create a signing instance.
	 *
	 * @param  input the signing instance input
	 * @return       {@link SigningInstance} containing the created signing instance
	 */
	@PostMapping(path = "/signing-instances", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	SigningInstance createSigningInstance(SigningInstanceInput input);

	/**
	 * Get a signing instance.
	 *
	 * @param  signingInstanceId the signing instance id
	 * @return                   {@link SigningInstance} containing the signing instance
	 */
	@GetMapping(path = "/signing-instances/{signingInstanceId}", produces = APPLICATION_JSON_VALUE)
	SigningInstance getSigningInstance(@PathVariable String signingInstanceId);

	/**
	 * Update a signing instance.
	 *
	 * @param  signingInstanceId the signing instance id
	 * @param  patch             the patch to apply
	 * @return                   {@link SigningInstance} containing the updated signing instance
	 */
	@PatchMapping(path = "/signing-instances/{signingInstanceId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	SigningInstance updateSigningInstance(@PathVariable String signingInstanceId, SigningInstancePatch patch);

	/**
	 * Search signing instance infos with filtering and pagination.
	 *
	 * @param  searchFilter the search filter with predicates and paginator
	 * @return              {@link SearchResult} containing matching signing instance infos
	 */
	@PostMapping(path = "/signing-instance-infos/search-filters", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	SearchResult searchSigningInstanceInfos(SearchFilter searchFilter);

	/**
	 * Get a signatory for a signing instance.
	 *
	 * @param  signingInstanceId the signing instance id
	 * @param  partyId           the party id of the signatory
	 * @return                   {@link Signatory} containing the signatory information
	 */
	@GetMapping(path = "/signing-instances/{signingInstanceId}/signatories/{partyId}", produces = APPLICATION_JSON_VALUE)
	Signatory getSignatory(@PathVariable String signingInstanceId, @PathVariable String partyId);
}
