package se.sundsvall.comfactfacade.integration.party;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.comfactfacade.integration.party.configuration.PartyConfiguration;

import static se.sundsvall.comfactfacade.integration.party.configuration.PartyConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.party.url}", configuration = PartyConfiguration.class)
public interface PartyClient {

	@GetMapping(path = "/{municipalityId}/{type}/{partyId}/legalId")
	String getLegalId(@PathVariable final String municipalityId, @PathVariable final String partyId, @PathVariable final String type);
}
