package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.SigningInstanceInput;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.sundsvall.comfactfacade.api.model.CreateSigningResponse;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.api.model.UpdateSigningRequest;
import se.sundsvall.comfactfacade.integration.comfact.ComfactIntegration;
import se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactAccountContext;
import se.sundsvall.comfactfacade.integration.party.PartyClient;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSearchFilter;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningInstanceInput;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningResponse;
import static se.sundsvall.comfactfacade.service.SigningMapper.toSigningsResponse;

@Service
public class SigningService {

	private final ComfactIntegration comfactIntegration;

	private final PartyClient partyClient;

	private final AccountService accountService;

	public SigningService(final ComfactIntegration comfactIntegration, final PartyClient partyClient, final AccountService accountService) {
		this.comfactIntegration = comfactIntegration;
		this.partyClient = partyClient;
		this.accountService = accountService;
	}

	public CreateSigningResponse createSigningRequest(final String municipalityId, final String accountKey, final SigningRequest signingRequest) {
		final var account = accountService.resolveAccount(municipalityId, accountKey);

		final var input = toSigningInstanceInput(signingRequest);
		Optional.ofNullable(account.comfactAccountId()).ifPresent(input::setAccountId);
		fetchPersonalNumbers(input, municipalityId);

		final var response = callWithAccount(account, () -> comfactIntegration.createSigningInstance(input));

		final var urlMap = new HashMap<String, String>();
		response.getSignatories().forEach(signatory -> Optional.ofNullable(signatory.getSignatoryUrl())
			.ifPresent(url -> urlMap.put(signatory.getPartyId(), url.trim())));

		return CreateSigningResponse.builder()
			.withSigningId(response.getSigningInstanceId())
			.withSignatoryUrls(urlMap)
			.build();
	}

	public void updateSigningRequest(final String municipalityId, final String accountKey, final String signingId, final UpdateSigningRequest updateSigningRequest) {
		final var account = accountService.resolveAccount(municipalityId, accountKey);
		callWithAccount(account, () -> {
			comfactIntegration.updateSigningInstance(signingId, SigningMapper.toSigningInstancePatch(updateSigningRequest));
			return null;
		});
	}

	public SigningInstance getSigningRequest(final String municipalityId, final String accountKey, final String signingId) {
		final var account = accountService.resolveAccount(municipalityId, accountKey);
		final var response = callWithAccount(account, () -> comfactIntegration.getSigningInstance(signingId));
		return toSigningResponse(response);
	}

	public SigningsResponse getSigningRequests(final String municipalityId, final String accountKey, final Pageable pageable) {
		final var account = accountService.resolveAccount(municipalityId, accountKey);
		final var response = callWithAccount(account, () -> comfactIntegration.searchSigningInstanceInfos(toSearchFilter(pageable)));
		return toSigningsResponse(response);
	}

	public Signatory getSignatory(final String municipalityId, final String accountKey, final String signingId, final String partyId) {
		final var account = accountService.resolveAccount(municipalityId, accountKey);
		final var response = callWithAccount(account, () -> comfactIntegration.getSignatory(signingId, partyId));
		return SigningMapper.toSignatory(response);
	}

	/**
	 * Runs the given Comfact call with the resolved account's credentials active on the thread.
	 */
	private <T> T callWithAccount(final AccountCredentials account, final Supplier<T> call) {
		try {
			ComfactAccountContext.set(account);
			return call.get();
		} finally {
			ComfactAccountContext.remove();
		}
	}

	private void fetchPersonalNumbers(final SigningInstanceInput input, final String municipalityId) {
		Optional.ofNullable(input.getSignatories())
			.ifPresent(signatories -> {

				final var partyIds = signatories.stream()
					.map(generated.se.sundsvall.comfact.Signatory::getPartyId)
					.filter(Objects::nonNull)
					.toList();

				if (!partyIds.isEmpty()) {
					final var legalIdsByPartyId = partyClient.getLegalIds(municipalityId, partyIds);

					// Check if we're missing any, in that case, throw an exception as we cannot continue.
					final var missingPartyIds = partyIds.stream()
						.filter(id -> !legalIdsByPartyId.containsKey(id))
						.toList();

					if (!missingPartyIds.isEmpty()) {
						throw Problem.valueOf(BAD_REQUEST, "Could not find legalId for partyId(s): " + missingPartyIds);
					}

					signatories.forEach(signatory -> signatory.setPersonalNumber(legalIdsByPartyId.get(signatory.getPartyId())));
				}
			});
	}
}
