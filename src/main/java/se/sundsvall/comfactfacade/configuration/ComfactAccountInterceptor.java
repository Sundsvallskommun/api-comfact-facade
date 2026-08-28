package se.sundsvall.comfactfacade.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import se.sundsvall.comfactfacade.service.AccountService;

import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import static se.sundsvall.comfactfacade.api.ApiConstants.ACCOUNT_KEY_HEADER;
import static se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactAccountContext.remove;
import static se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactAccountContext.set;

/**
 * Resolves the Comfact account to use for the current request - from the x-account-key header, or the municipality's
 * default account - and exposes it via ComfactAccountContext for the duration of the request. Registered only for the
 * signing endpoints (see WebMvcConfiguration); the account CRUD endpoints must stay outside it, or the first account
 * could never be created.
 */
public class ComfactAccountInterceptor implements HandlerInterceptor {

	private static final String MUNICIPALITY_ID = "municipalityId";

	private final AccountService accountService;

	public ComfactAccountInterceptor(final AccountService accountService) {
		this.accountService = accountService;
	}

	@Override
	public boolean preHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler) {
		set(accountService.resolveAccount(municipalityId(request), request.getHeader(ACCOUNT_KEY_HEADER)));
		return true;
	}

	@Override
	public void afterCompletion(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler, final Exception exception) {
		remove();
	}

	private String municipalityId(final HttpServletRequest request) {
		final var pathVariables = (Map<?, ?>) request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		return Optional.ofNullable(pathVariables)
			.map(vars -> (String) vars.get(MUNICIPALITY_ID))
			.orElse(null);
	}
}
