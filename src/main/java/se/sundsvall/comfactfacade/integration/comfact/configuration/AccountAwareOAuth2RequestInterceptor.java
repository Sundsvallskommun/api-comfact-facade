package se.sundsvall.comfactfacade.integration.comfact.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import se.sundsvall.comfactfacade.service.AccountCredentials;
import se.sundsvall.dept44.configuration.feign.interceptor.OAuth2RequestInterceptor;

/**
 * Applies an OAuth2 bearer token for the Comfact account selected via {@link ComfactAccountContext}. All credentials
 * are data-driven - there is no static fallback, so an unset context is a programming error. One dept44
 * {@link OAuth2RequestInterceptor} (with its own token cache) is kept per distinct credential pair; the cache key
 * includes a hash of the credentials so a rotated secret transparently gets a fresh delegate.
 */
public class AccountAwareOAuth2RequestInterceptor implements RequestInterceptor {

	private final String tokenUrl;
	private final Map<String, OAuth2RequestInterceptor> delegates = new ConcurrentHashMap<>();

	public AccountAwareOAuth2RequestInterceptor(final String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	@Override
	public void apply(final RequestTemplate requestTemplate) {
		final var credentials = ComfactAccountContext.get()
			.orElseThrow(() -> new IllegalStateException("No Comfact account credentials set on the current thread"));
		delegates.computeIfAbsent(cacheKey(credentials), _ -> new OAuth2RequestInterceptor(toClientRegistration(credentials), Set.of()))
			.apply(requestTemplate);
	}

	/**
	 * Evicts the token that caused a failing request from every delegate. Each delegate compares the failing token to
	 * its cached one, so this is a no-op for all but the delegate that issued it.
	 */
	public void removeToken(final String failedAuthorizationHeader) {
		delegates.values().forEach(delegate -> delegate.removeToken(failedAuthorizationHeader));
	}

	private String cacheKey(final AccountCredentials credentials) {
		return credentials.id() + ":" + Objects.hash(credentials.clientId(), credentials.clientSecret());
	}

	private ClientRegistration toClientRegistration(final AccountCredentials credentials) {
		// The Comfact token endpoint doesn't accept Basic auth with special characters in the client-id,
		// hence client_secret_post.
		return ClientRegistration.withRegistrationId("comfact-account-" + credentials.id())
			.tokenUri(tokenUrl)
			.clientId(credentials.clientId())
			.clientSecret(credentials.clientSecret())
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
			.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
			.build();
	}
}
