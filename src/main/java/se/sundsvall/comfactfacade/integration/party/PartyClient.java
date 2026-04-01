package se.sundsvall.comfactfacade.integration.party;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.comfactfacade.integration.party.configuration.PartyConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.comfactfacade.integration.party.configuration.PartyConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.party.url}", configuration = PartyConfiguration.class)
public interface PartyClient {

	@PostMapping(path = "/{municipalityId}/PRIVATE/legalIds", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	Map<String, String> getLegalIds(
		@PathVariable final String municipalityId,
		@RequestBody final List<String> partyIds);
}
