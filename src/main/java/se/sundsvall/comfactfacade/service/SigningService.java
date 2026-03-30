package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.SigningInstanceInput;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;
import se.sundsvall.comfactfacade.integration.party.PartyClient;

import static se.sundsvall.comfactfacade.service.SigningMapper.toSearchFilter;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningInstanceInput;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningResponse;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningsResponse;

@Service
public class SigningService {

	private final ComfactIntegration comfactIntegration;

	private final PartyClient partyClient;

	public SigningService(final ComfactIntegration comfactIntegration, final PartyClient partyClient) {
		this.comfactIntegration = comfactIntegration;
		this.partyClient = partyClient;
	}

	public CreateSigningResponse createSigningRequest(final String municipalityId, final SigningRequest signingRequest) {
		final var input = toSigningInstanceInput(signingRequest);
		fetchPersonalNumbers(input, municipalityId);

		final var response = comfactIntegration.createSigningInstance(input);

		final var urlMap = new HashMap<String, String>();
		response.getSignatories().forEach(signatory -> Optional.ofNullable(signatory.getSignatoryUrl())
			.ifPresent(url -> urlMap.put(signatory.getPartyId(), url.trim())));

		return CreateSigningResponse.builder()
			.withSigningId(response.getSigningInstanceId())
			.withSignatoryUrls(urlMap)
			.build();
	}

	public void updateSigningRequest(final String municipalityId, final String signingId, final SigningRequest signingRequest) {
		final var input = toSigningInstanceInput(signingRequest);
		fetchPersonalNumbers(input, municipalityId);

		// For update, we create a new signing instance with the updated data
		// The REST API uses PATCH for updates with SigningInstancePatch
		comfactIntegration.updateSigningInstance(signingId, SigningMapper.toSigningInstancePatch(signingId, signingRequest));
	}

	public void cancelSigningRequest(final String municipalityId, final String signingId) {
		comfactIntegration.withdrawSigningInstance(signingId);
	}

	public SigningInstance getSigningRequest(final String municipalityId, final String signingId) {
		final var response = comfactIntegration.getSigningInstance(signingId);
		return toSigningResponse(response);
	}

	public SigningsResponse getSigningRequests(final String municipalityId, final Pageable pageable) {
		final var response = comfactIntegration.searchSigningInstanceInfos(toSearchFilter(pageable));
		return toSigningsResponse(response);
	}

	public Signatory getSignatory(final String municipalityId, final String signingId, final String partyId) {
		final var response = comfactIntegration.getSignatory(signingId, partyId);
		return SigningMapper.toSignatory(response);
	}

	private void fetchPersonalNumbers(final SigningInstanceInput input, final String municipalityId) {
		Optional.ofNullable(input.getSignatories())
			.ifPresent(signatories -> signatories.forEach(signatory -> {
				final var legalId = partyClient.getLegalId(municipalityId, signatory.getPartyId(), "PRIVATE");
				signatory.setPersonalNumber(legalId);
			}));
	}
}
