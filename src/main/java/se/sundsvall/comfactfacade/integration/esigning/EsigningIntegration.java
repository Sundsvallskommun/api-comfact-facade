package se.sundsvall.comfactfacade.integration.esigning;

import org.springframework.stereotype.Service;
import se.sundsvall.comfactfacade.integration.esigning.model.ComfactEventNotification;

@Service
public class EsigningIntegration {

	private final EsigningClient esigningClient;

	public EsigningIntegration(final EsigningClient esigningClient) {
		this.esigningClient = esigningClient;
	}

	public void forwardComfactEvent(final String municipalityId, final ComfactEventNotification event) {
		esigningClient.forwardComfactEvent(municipalityId, event);
	}
}
