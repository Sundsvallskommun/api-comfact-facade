package se.sundsvall.comfactfacade.integration.comfact;

import org.springframework.stereotype.Service;

import comfact.CreateSigningInstanceRequest;
import comfact.CreateSigningInstanceResponse;
import comfact.Credentials;
import comfact.GetSignatoryRequest;
import comfact.GetSignatoryResponse;
import comfact.GetSigningInstanceInfoRequest;
import comfact.GetSigningInstanceInfoResponse;
import comfact.GetSigningInstanceRequest;
import comfact.GetSigningInstanceResponse;
import comfact.UpdateSigningInstanceRequest;
import comfact.WithdrawSigningInstanceRequest;
import se.sundsvall.comfactfacade.configuration.MunicipalityProperties;

@Service
public class ComfactIntegration {

	private final ComfactClient comfactClient;

	private final MunicipalityProperties municipalityProperties;

	public ComfactIntegration(final ComfactClient comfactClient,
		final MunicipalityProperties municipalityProperties) {
		this.comfactClient = comfactClient;
		this.municipalityProperties = municipalityProperties;
	}

	private Credentials toCredentials(final String municipalityId) {
		final var properties = municipalityProperties.ids().get(municipalityId);
		return new comfact.Credentials().withPassword(properties.password()).withUserId(properties.username());
	}

	public CreateSigningInstanceResponse createSigningInstance(final String municipalityId, final CreateSigningInstanceRequest request) {
		return comfactClient.createSigningInstance(request.withCredentials(toCredentials(municipalityId)));
	}

	public void updateSigningInstance(final String municipalityId, final UpdateSigningInstanceRequest request) {
		comfactClient.updateSigningInstance(request.withCredentials(toCredentials(municipalityId)));
	}

	public void withdrawSigningInstance(final String municipalityId, final WithdrawSigningInstanceRequest request) {
		comfactClient.withdrawSigningInstance(request.withCredentials(toCredentials(municipalityId)));
	}

	public GetSigningInstanceResponse getSigningInstance(final String municipalityId, final GetSigningInstanceRequest request) {
		return comfactClient.getSigningInstance(request.withCredentials(toCredentials(municipalityId)));
	}

	public GetSigningInstanceInfoResponse getSigningInstanceInfo(final String municipalityId, final GetSigningInstanceInfoRequest request) {
		return comfactClient.getSigningInstanceInfo(request.withCredentials(toCredentials(municipalityId)));
	}

	public GetSignatoryResponse getSignatory(final String municipalityId, final GetSignatoryRequest request) {
		return comfactClient.getSignatory(request.withCredentials(toCredentials(municipalityId)));
	}

}
