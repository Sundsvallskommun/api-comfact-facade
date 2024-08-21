package se.sundsvall.comfactfacade.service;

import static se.sundsvall.comfactfacade.service.SigningMapper.toCreateSigningInstanceRequestType;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningResponse;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningsResponse;
import static se.sundsvall.comfactfacade.service.SigningMapper.toUpdateSigningInstanceRequestType;
import static se.sundsvall.comfactfacade.service.SigningMapper.toWithdrawSigningInstanceRequestType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;
import se.sundsvall.comfactfacade.integration.party.PartyClient;

import comfact.GetSignatoryRequest;
import comfact.GetSigningInstanceRequest;
import comfact.SigningInstanceInputType;

@Service
public class SigningService {

	private final ComfactIntegration comfactIntegration;

	private final PartyClient partyClient;


	public SigningService(final ComfactIntegration comfactIntegration, final PartyClient partyClient) {
		this.comfactIntegration = comfactIntegration;
		this.partyClient = partyClient;
	}

	public CreateSigningResponse createSigningRequest(final String municipalityId, final SigningRequest signingRequest) {
		final var request = toCreateSigningInstanceRequestType(signingRequest);
		fetchPersonalNumbers(request.getSigningInstanceInput());

		final var response = comfactIntegration.createSigningInstance(municipalityId, request);

		final var urlMap = new HashMap<String, String>();
		response.getSignatoryUrls().forEach(signatoryUrl -> urlMap.put(signatoryUrl.getPartyId(), signatoryUrl.getValue().trim()));

		return CreateSigningResponse.builder()
			.withSigningId(response.getSigningInstanceId())
			.withSignatoryUrls(urlMap)
			.build();
	}

	public void updateSigningRequest(final String municipalityId, final String signingId, final SigningRequest signingRequest) {
		final var request = toUpdateSigningInstanceRequestType(signingId, signingRequest);
		fetchPersonalNumbers(request.getSigningInstanceInput());

		comfactIntegration.updateSigningInstance(municipalityId, request);
	}

	public void cancelSigningRequest(final String municipalityId, final String signingId) {
		comfactIntegration.withdrawSigningInstance(municipalityId, toWithdrawSigningInstanceRequestType(signingId));
	}

	public SigningInstance getSigningRequest(final String municipalityId, final String signingId) {
		final var response = comfactIntegration.getSigningInstance(municipalityId, new GetSigningInstanceRequest().withSigningInstanceId(signingId));
		return toSigningResponse(response);
	}

	public SigningsResponse getSigningRequests(final String municipalityId, final Pageable pageable) {

		final var response = comfactIntegration.getSigningInstanceInfo(municipalityId, SigningMapper.toGetSigningInstanceInfoRequest(pageable));
		return toSigningsResponse(response);
	}

	public se.sundsvall.comfactfacade.api.model.Signatory getSignatory(final String municipalityId, final String signingId, final String partyId) {
		final var response = comfactIntegration.getSignatory(municipalityId, new GetSignatoryRequest().withPartyId(partyId).withSigningInstanceId(signingId));
		return SigningMapper.toSignatory(response.getSignatories().getFirst());

	}


	private void fetchPersonalNumbers(final SigningInstanceInputType request) {

		processEntities(request.getSignatories());

		Optional.ofNullable(request.getInitiator())
			.ifPresent(this::processEntity);

		processEntities(request.getAdditionalParties());
	}

	private void processEntities(final List<? extends comfact.PartyType> partyTypes) {
		Optional.ofNullable(partyTypes)
			.ifPresent(parties -> parties.forEach(this::processEntity));
	}

	private void processEntity(final comfact.PartyType partyType) {
		Optional.ofNullable(partyType)
			.ifPresent(party -> {
				final var legalId = partyClient.getLegalId(party.getPartyId(), "PRIVATE");
				party.setPersonalNumber(legalId);
			});
	}

}
