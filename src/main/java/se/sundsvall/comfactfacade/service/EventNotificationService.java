package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.EventNotification;
import org.springframework.stereotype.Service;
import se.sundsvall.comfactfacade.integration.esigning.EsigningIntegration;

@Service
public class EventNotificationService {

	private final EsigningIntegration esigningIntegration;

	public EventNotificationService(final EsigningIntegration esigningIntegration) {
		this.esigningIntegration = esigningIntegration;
	}

	public void handleComfactEvent(final String municipalityId, final EventNotification notification) {
		esigningIntegration.forwardComfactEvent(municipalityId, EventNotificationMapper.toEsigningEvent(notification));
	}
}
