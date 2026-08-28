package se.sundsvall.comfactfacade.integration.comfact.configuration;

import java.util.Optional;
import se.sundsvall.comfactfacade.service.AccountCredentials;

/**
 * Thread-local holder for the Comfact account credentials to use for the current request. When unset, the statically
 * configured 'comfact' client registration is used. Set by the service layer around Comfact calls - always in a
 * try/finally so the thread is left clean for reuse.
 */
public final class ComfactAccountContext {

	private static final ThreadLocal<AccountCredentials> CONTEXT = new ThreadLocal<>();

	private ComfactAccountContext() {}

	public static void set(final AccountCredentials credentials) {
		CONTEXT.set(credentials);
	}

	public static Optional<AccountCredentials> get() {
		return Optional.ofNullable(CONTEXT.get());
	}

	public static void remove() {
		CONTEXT.remove();
	}
}
