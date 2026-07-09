package se.sundsvall.comfactfacade.integration.esigning.model;

/**
 * A signatory referenced by a Comfact event, forwarded to api-service-e-signing.
 *
 * @param partyId the party that acted
 * @param action  the Comfact action ({@code approved} / {@code declined}), when present
 * @param reason  the reason given for the action, when present
 */
public record ComfactSignatory(String partyId, String action, String reason) {
}
