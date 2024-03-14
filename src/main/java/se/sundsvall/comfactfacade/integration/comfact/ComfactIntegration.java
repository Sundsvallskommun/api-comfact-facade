package se.sundsvall.comfactfacade.integration.comfact;


import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

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
import comfact.ResponseType;
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

	private void handleResponse(final ResponseType response) {
		if (!response.getResult().isSuccess()) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, response.getResult().getResultMessage());
		}
	}

	private Credentials toCredentials() {
		return new comfact.Credentials().withPassword(comfactProperties.password()).withUserId(comfactProperties.username());
	}

	public CreateSigningInstanceResponse createSigningInstance(final CreateSigningInstanceRequest request) {
		final var response = comfactClient.createSigningInstance(request.withCredentials(toCredentials()));
		handleResponse(response);
		return response;
	}

	public void updateSigningInstance(final UpdateSigningInstanceRequest request) {
		handleResponse(comfactClient.updateSigningInstance(request.withCredentials(toCredentials())));
	}

	public void withdrawSigningInstance(final WithdrawSigningInstanceRequest request) {
		handleResponse(comfactClient.withdrawSigningInstance(request.withCredentials(toCredentials())));
	}

	public GetSigningInstanceResponse getSigningInstance(final GetSigningInstanceRequest request) {
		final var response = comfactClient.getSigningInstance(request.withCredentials(toCredentials()));
		handleResponse(response);
		return response;
	}

	public GetSigningInstanceInfoResponse getSigningInstanceInfo(final GetSigningInstanceInfoRequest request) {
		final var response = comfactClient.getSigningInstanceInfo(request.withCredentials(toCredentials()));
		handleResponse(response);
		return response;
	}

	public GetSignatoryResponse getSignatory(final GetSignatoryRequest request) {
		final var response = comfactClient.getSignatory(request.withCredentials(toCredentials()));
		handleResponse(response);
		return response;
	}

}
