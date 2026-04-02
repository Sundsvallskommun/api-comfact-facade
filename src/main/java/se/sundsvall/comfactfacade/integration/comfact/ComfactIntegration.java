package se.sundsvall.comfactfacade.integration.comfact;

import generated.se.sundsvall.comfact.SearchFilter;
import generated.se.sundsvall.comfact.SearchResult;
import generated.se.sundsvall.comfact.Signatory;
import generated.se.sundsvall.comfact.SigningInstance;
import generated.se.sundsvall.comfact.SigningInstanceInput;
import generated.se.sundsvall.comfact.SigningInstancePatch;
import org.springframework.stereotype.Service;

@Service
public class ComfactIntegration {

	private final ComfactClient comfactClient;

	public ComfactIntegration(final ComfactClient comfactClient) {
		this.comfactClient = comfactClient;
	}

	public SigningInstance createSigningInstance(final SigningInstanceInput input) {
		return comfactClient.createSigningInstance(input);
	}

	public SigningInstance updateSigningInstance(final String signingInstanceId, final SigningInstancePatch patch) {
		return comfactClient.updateSigningInstance(signingInstanceId, patch);
	}

	public SigningInstance getSigningInstance(final String signingInstanceId) {
		return comfactClient.getSigningInstance(signingInstanceId);
	}

	public SearchResult searchSigningInstanceInfos(final SearchFilter searchFilter) {
		return comfactClient.searchSigningInstanceInfos(searchFilter);
	}

	public Signatory getSignatory(final String signingInstanceId, final String partyId) {
		return comfactClient.getSignatory(signingInstanceId, partyId);
	}
}
