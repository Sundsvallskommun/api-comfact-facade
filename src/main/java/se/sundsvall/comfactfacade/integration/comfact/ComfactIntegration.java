package se.sundsvall.comfactfacade.integration.comfact;


import org.springframework.stereotype.Service;

import se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactProperties;

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

@Service
public class ComfactIntegration {

	private final ComfactClient comfactClient;

	private final ComfactProperties comfactProperties;


	public ComfactIntegration(final ComfactClient comfactClient, final ComfactProperties comfactProperties) {
		this.comfactClient = comfactClient;
		this.comfactProperties = comfactProperties;
	}

	private Credentials toCredentials() {
		return new comfact.Credentials().withPassword(comfactProperties.password()).withUserId(comfactProperties.username());
	}

	public CreateSigningInstanceResponse createSigningInstance(final CreateSigningInstanceRequest request) {
		return comfactClient.createSigningInstance(request.withCredentials(toCredentials()));
	}

	public void updateSigningInstance(final UpdateSigningInstanceRequest request) {
		comfactClient.updateSigningInstance(request.withCredentials(toCredentials()));
	}

	public void withdrawSigningInstance(final WithdrawSigningInstanceRequest request) {
		comfactClient.withdrawSigningInstance(request.withCredentials(toCredentials()));
	}

	public GetSigningInstanceResponse getSigningInstance(final GetSigningInstanceRequest request) {
		return comfactClient.getSigningInstance(request.withCredentials(toCredentials()));
	}

	public GetSigningInstanceInfoResponse getSigningInstanceInfo(final GetSigningInstanceInfoRequest request) {
		return comfactClient.getSigningInstanceInfo(request.withCredentials(toCredentials()));
	}

	public GetSignatoryResponse getSignatory(final GetSignatoryRequest request) {
		return comfactClient.getSignatory(request.withCredentials(toCredentials()));
	}

}
