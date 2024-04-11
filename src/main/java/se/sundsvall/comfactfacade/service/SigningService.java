package se.sundsvall.comfactfacade.service;

import static se.sundsvall.comfactfacade.service.SigningMapper.toCreateSigningInstanceRequestType;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningResponse;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningsResponse;
import static se.sundsvall.comfactfacade.service.SigningMapper.toUpdateSigningInstanceRequestType;
import static se.sundsvall.comfactfacade.service.SigningMapper.toWithdrawSigningInstanceRequestType;

import java.util.HashMap;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;

import comfact.GetSignatoryRequest;
import comfact.GetSigningInstanceRequest;


@Service
public class SigningService {

	private final ComfactIntegration comfactIntegration;

	public SigningService(final ComfactIntegration comfactIntegration) {
		this.comfactIntegration = comfactIntegration;
	}

	public CreateSigningResponse createSigningRequest(final SigningRequest signingRequest) {
		final var response = comfactIntegration.createSigningInstance(toCreateSigningInstanceRequestType(signingRequest));

		final var urlMap = new HashMap<String, String>();
		response.getSignatoryUrls().forEach(signatoryUrl -> urlMap.put(signatoryUrl.getPartyId(), signatoryUrl.getValue().trim()));

		return CreateSigningResponse.builder()
			.withSigningId(response.getSigningInstanceId())
			.withSignatoryUrls(urlMap)
			.build();
	}


	public void updateSigningRequest(final String signingId, final SigningRequest signingRequest) {
		comfactIntegration.updateSigningInstance(toUpdateSigningInstanceRequestType(signingId, signingRequest));
	}

	public void cancelSigningRequest(final String signingId) {
		comfactIntegration.withdrawSigningInstance(toWithdrawSigningInstanceRequestType(signingId));
	}

	public SigningInstance getSigningRequest(final String signingId) {
		final var response = comfactIntegration.getSigningInstance(new GetSigningInstanceRequest().withSigningInstanceId(signingId));
		return toSigningResponse(response);
	}

	public SigningsResponse getSigningRequests(final Pageable pageable) {

		final var response = comfactIntegration.getSigningInstanceInfo(SigningMapper.toGetSigningInstanceInfoRequest(pageable));
		return toSigningsResponse(response);
	}

	public se.sundsvall.comfactfacade.api.model.Signatory getSignatory(final String signingId, final String partyId) {
		final var response = comfactIntegration.getSignatory(new GetSignatoryRequest().withPartyId(partyId).withSigningInstanceId(signingId));
		return SigningMapper.toSignatory(response.getSignatories().getFirst());

	}


}
