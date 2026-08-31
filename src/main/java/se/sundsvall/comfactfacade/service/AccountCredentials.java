package se.sundsvall.comfactfacade.service;

/**
 * Resolved credentials for a Comfact account, with the client secret decrypted. The comfactAccountId is optional and,
 * when present, is sent as accountId on the signing instance.
 *
 * @param id               the database id of the account (used as token cache key)
 * @param clientId         the OAuth2 client id
 * @param clientSecret     the decrypted OAuth2 client secret
 * @param comfactAccountId the optional Comfact account configuration id
 */
public record AccountCredentials(String id, String clientId, String clientSecret, String comfactAccountId) {

	@Override
	public String toString() {
		return "AccountCredentials[id=%s, clientId=%s, clientSecret=[hidden], comfactAccountId=%s]".formatted(id, clientId, comfactAccountId);
	}
}
