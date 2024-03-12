package se.sundsvall.comfactfacade;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Identification;
import se.sundsvall.comfactfacade.api.model.NotificationMessage;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.SigningRequest;

public class TestUtil {


	/**
	 * This is a builder for creating test instances of the SigningRequest class.
	 *
	 * <p>
	 * It provides default values for all fields, which can be overridden by calling the corresponding method.
	 * The build() method generates a SigningRequest instance with the current builder configuration.
	 *
	 * <p>
	 * The builder also includes a method for modifying the Party object's fields.
	 * The Party object is utilized for the initiator, additionalParty, and signatory fields in the SigningRequest.
	 */
	public static class SigningRequestBuilder {

		private final List<Document> additionalDocuments = new ArrayList<>();

		private final OffsetDateTime expires = OffsetDateTime.now();

		private final NotificationMessage notificationMessage = new NotificationMessage("defaultSubject", "defaultBody", "sv-SE");

		public Party defaultParty = new Party("defaultName", "550e8400-e29b-41d4-a716-446655440000", "defaultTitle", "197001010101", "defaultEmail", "0701234567", "default", "defaultLanguage", new Identification("defaultAlias"));

		private Party initiator = defaultParty; // Default to defaultParty if not specified

		private Party additionalParty = defaultParty; // Default to defaultParty if not specified

		private Party signatory = defaultParty; // Default to defaultParty if not specified

		private Document document = new Document("defaultName", "defaultFileName", "defaultContentType", "defaultContent");

		public SigningRequestBuilder withInitiator(final Party initiator) {
			this.initiator = initiator;
			return this;
		}

		public SigningRequestBuilder withAdditionalParty(final Party additonalParty) {
			this.additionalParty = additonalParty;
			return this;
		}

		public SigningRequestBuilder withSignatory(final Party signatory) {
			this.signatory = signatory;
			return this;
		}

		public SigningRequestBuilder withDocument(final Document document) {
			this.document = document;
			return this;
		}

		public SigningRequestBuilder withPartyProp(final String field, final Object value) {
			switch (field) {
				case "name" ->
					defaultParty = new Party((String) value, defaultParty.partyId(), defaultParty.title(), defaultParty.personalNumber(), defaultParty.email(), defaultParty.phoneNumber(), defaultParty.organization(), defaultParty.language(), defaultParty.identification());
				case "partyId" ->
					defaultParty = new Party(defaultParty.name(), (String) value, defaultParty.title(), defaultParty.personalNumber(), defaultParty.email(), defaultParty.phoneNumber(), defaultParty.organization(), defaultParty.language(), defaultParty.identification());
				case "title" ->
					defaultParty = new Party(defaultParty.name(), defaultParty.partyId(), (String) value, defaultParty.personalNumber(), defaultParty.email(), defaultParty.phoneNumber(), defaultParty.organization(), defaultParty.language(), defaultParty.identification());
				case "personalNumber" ->
					defaultParty = new Party(defaultParty.name(), defaultParty.partyId(), defaultParty.title(), (String) value, defaultParty.email(), defaultParty.phoneNumber(), defaultParty.organization(), defaultParty.language(), defaultParty.identification());
				case "email" ->
					defaultParty = new Party(defaultParty.name(), defaultParty.partyId(), defaultParty.title(), defaultParty.personalNumber(), (String) value, defaultParty.phoneNumber(), defaultParty.organization(), defaultParty.language(), defaultParty.identification());
				case "phoneNumber" ->
					defaultParty = new Party(defaultParty.name(), defaultParty.partyId(), defaultParty.title(), defaultParty.personalNumber(), defaultParty.email(), (String) value, defaultParty.organization(), defaultParty.language(), defaultParty.identification());
				case "organization" ->
					defaultParty = new Party(defaultParty.name(), defaultParty.partyId(), defaultParty.title(), defaultParty.personalNumber(), defaultParty.email(), defaultParty.phoneNumber(), (String) value, defaultParty.language(), defaultParty.identification());
				case "language" ->
					defaultParty = new Party(defaultParty.name(), defaultParty.partyId(), defaultParty.title(), defaultParty.personalNumber(), defaultParty.email(), defaultParty.phoneNumber(), defaultParty.organization(), (String) value, defaultParty.identification());
				case "identification" ->
					defaultParty = new Party(defaultParty.name(), defaultParty.partyId(), defaultParty.title(), defaultParty.personalNumber(), defaultParty.email(), defaultParty.phoneNumber(), defaultParty.organization(), defaultParty.language(), (Identification) value);
				default -> throw new IllegalArgumentException("Field '" + field + "' not recognized.");
			}
			return this;
		}

		public SigningRequest build() {
			final String customerReference = "defaultCustomerReference";
			final String language = "defaultLanguage";
			return new SigningRequest(customerReference, expires, notificationMessage, initiator, additionalParty, signatory, document, additionalDocuments, language);
		}


	}

}
