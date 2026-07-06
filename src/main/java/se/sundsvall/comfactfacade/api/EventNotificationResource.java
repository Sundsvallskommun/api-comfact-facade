package se.sundsvall.comfactfacade.api;

import generated.se.sundsvall.comfact.EventNotification;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.comfactfacade.service.EventNotificationService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Inbound webhook receiver for Comfact provider events. Called by Comfact only (account-global webhook target,
 * with the municipality baked into the URL), never by other Sundsvall services — hence hidden from the public
 * OpenAPI. Received events are forwarded, unchanged, to api-service-e-signing for normalization.
 */
@Hidden
@RestController
@Validated
@RequestMapping("/{municipalityId}/webhooks")
class EventNotificationResource {

	private final EventNotificationService eventNotificationService;

	EventNotificationResource(final EventNotificationService eventNotificationService) {
		this.eventNotificationService = eventNotificationService;
	}

	@PostMapping(path = "/comfact", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> handleComfactEvent(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@RequestBody final EventNotification notification) {
		eventNotificationService.handleComfactEvent(municipalityId, notification);
		return ok().build();
	}
}
