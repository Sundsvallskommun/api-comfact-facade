package se.sundsvall.comfactfacade.integration.esigning;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import se.sundsvall.comfactfacade.integration.esigning.configuration.EsigningConfiguration;
import se.sundsvall.comfactfacade.integration.esigning.model.ComfactEventNotification;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.comfactfacade.integration.esigning.configuration.EsigningConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.esigning.url}", configuration = EsigningConfiguration.class)
public interface EsigningClient {

	/**
	 * Forward a Comfact signing event to api-service-e-signing.
	 *
	 * @param municipalityId the municipality id
	 * @param event          the flattened Comfact event
	 */
	@PostMapping(path = "/{municipalityId}/e-signing/webhooks/comfact", consumes = APPLICATION_JSON_VALUE)
	void forwardComfactEvent(@PathVariable String municipalityId, ComfactEventNotification event);
}
