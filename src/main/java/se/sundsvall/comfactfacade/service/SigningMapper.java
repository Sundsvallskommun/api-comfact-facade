package se.sundsvall.comfactfacade.service;

import static java.util.Collections.emptyList;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.zalando.problem.Problem;

import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Identification;
import se.sundsvall.comfactfacade.api.model.NotificationMessage;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.Reminder;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.Status;

import comfact.CreateSigningInstanceRequest;
import comfact.DocumentType;
import comfact.GetSigningInstanceResponse;
import comfact.MessageType;
import comfact.PartyType;
import comfact.SignatoryReminder;
import comfact.SigningInstanceInfo;
import comfact.SigningInstanceInputType;
import comfact.UpdateSigningInstanceRequest;
import comfact.WithdrawSigningInstanceRequest;

public final class SigningMapper {

	private SigningMapper() {
		// Intentionally left empty
	}

	static SigningInstance toSigningResponse(final GetSigningInstanceResponse response) {

		return SigningInstance.builder()
			.withStatus(toStatus(response.getSigningInstance().getStatus()))
			.withCustomerReference(response.getSigningInstance().getCustomerReferenceNumber())
			.withCreated(toOffsetDateTime(response.getSigningInstance().getCreated()))
			.withChanged(toOffsetDateTime(response.getSigningInstance().getChanged()))
			.withDocument(toDocument(response.getSigningInstance().getDocument()))
			.withInitiator(toParty(response.getSigningInstance().getInitiator()))
			.withSignatories(response.getSigningInstance().getSignatories().stream().map(SigningMapper::toParty).toList())
			.withAdditionalParties(response.getSigningInstance().getAdditionalParties().stream().map(SigningMapper::toParty).toList())
			.withAdditionalDocuments(response.getSigningInstance().getDocumentAttachments().stream().map(SigningMapper::toDocument).toList())
			.withSigningId(response.getSigningInstance().getSigningInstanceId())
			.withExpires(toOffsetDateTime(response.getSigningInstance().getExpires()))
			.withNotificationMessages(response.getSigningInstance().getNotificationMessages().stream().map(SigningMapper::toNotificationMessage).toList())
			.withSignedDocument(toDocument(response.getSigningInstance().getSignedDocument()))
			.build();
	}

	static NotificationMessage toNotificationMessage(final MessageType notificationMessages) {
		if (notificationMessages == null) {
			return null;
		}
		return NotificationMessage.builder()
			.withSubject(notificationMessages.getSubject())
			.withBody(notificationMessages.getBody())
			.withLanguage(notificationMessages.getLanguage())
			.build();
	}

	static Party toParty(final PartyType partyType) {
		if (partyType == null) {
			return null;
		}

		final var party = Party.builder()
			.withEmail(partyType.getEmailAddress())
			.withName(partyType.getName())
			.withOrganization(partyType.getOrganization())
			.withPartyId(partyType.getPartyId())
			.withPersonalNumber(partyType.getPersonalNumber())
			.withPhoneNumber(partyType.getMobilePhoneNumber())
			.withTitle(partyType.getTitle())
			.withLanguage(partyType.getLanguage());
		if (partyType instanceof final comfact.Signatory signatory) {
			party.withIdentifications(signatory.getIdentifications().stream().map(identification ->
				new Identification(identification.getAlias())).toList());
			party.withNotificationMessage(toNotificationMessage(signatory.getNotificationMessage()));
		}

		return party.build();

	}

	static SigningInstance toSigningInstanceInfoType(final SigningInstanceInfo response) {
		return SigningInstance.builder()
			.withStatus(toStatus(response.getStatus()))
			.withCustomerReference(response.getCustomerReferenceNumber())
			.withCreated(toOffsetDateTime(response.getCreated()))
			.withChanged(toOffsetDateTime(response.getChanged()))
			.withExpires(toOffsetDateTime(response.getExpires()))
			.withSigningId(response.getSigningInstanceId())
			.build();

	}

	static WithdrawSigningInstanceRequest toWithdrawSigningInstanceRequestType(final String signingId) {
		return new WithdrawSigningInstanceRequest().withSigningInstanceId(signingId);
	}

	static UpdateSigningInstanceRequest toUpdateSigningInstanceRequestType(final String signingId, final SigningRequest signingRequest) {
		return new UpdateSigningInstanceRequest()
			.withSigningInstanceId(signingId)
			.withSigningInstanceInput(toSigningInstanceInputType(signingRequest)
			);
	}

	static SigningInstanceInputType toSigningInstanceInputType(final SigningRequest signingRequest) {
		return new SigningInstanceInputType()
			.withCustomerReferenceNumber(signingRequest.getCustomerReference())
			.withExpires(toXMLGregorianCalendar(signingRequest.getExpires()))
			.withNotificationMessages(toMessageType(signingRequest.getNotificationMessage()))
			.withSignatoryReminder(toSignatoryReminderType(signingRequest.getReminder()))
			.withInitiator(toPartyType(signingRequest.getInitiator()))
			.withAdditionalParties(toPartyType(signingRequest.getAdditionalParty()))
			.withSignatories(toSignatoryType(signingRequest.getSignatory()))
			.withLanguage(signingRequest.getLanguage())
			.withDocument(toDocumentType(signingRequest.getDocument()))
			.withDocumentAttachments(toDocumentTypeList(signingRequest.getAdditionalDocuments()));
	}

	static CreateSigningInstanceRequest toCreateSigningInstanceRequestType(final SigningRequest signingRequest) {
		return new CreateSigningInstanceRequest().withSigningInstanceInput(toSigningInstanceInputType(signingRequest));
	}

	static comfact.Signatory toSignatoryType(final Party party) {
		if (party == null) {
			return null;
		}
		return new comfact.Signatory()
			.withName(party.getName())
			.withTitle(party.getTitle())
			.withOrganization(party.getOrganization())
			.withPersonalNumber(party.getPersonalNumber())
			.withEmailAddress(party.getEmail())
			.withMobilePhoneNumber(party.getPhoneNumber())
			.withPartyId(party.getPartyId())
			.withNotificationMessage(toMessageType(party.getNotificationMessage()))
			.withIdentifications(
				Optional.ofNullable(party.getIdentifications()).stream().flatMap(List::stream).map(identification -> new comfact.Identification().withAlias(identification.getAlias())).toList())
			.withLanguage(party.getLanguage());
	}

	static MessageType toMessageType(final NotificationMessage notificationMessage) {
		if (notificationMessage == null) {
			return null;
		}
		return new MessageType()
			.withSubject(notificationMessage.getSubject())
			.withBody(notificationMessage.getBody())
			.withLanguage(notificationMessage.getLanguage());
	}

	static SignatoryReminder toSignatoryReminderType(final Reminder reminder) {
		if (reminder == null) {
			return null;
		}
		return new SignatoryReminder()
			.withReminderMessages(toMessageType(reminder.getMessage()))
			.withEnabled(reminder.isEnabled())
			.withHourInterval(reminder.getIntervalInHours())
			.withStartDate(toXMLGregorianCalendar(reminder.getStartDateTime()));
	}

	static PartyType toPartyType(final Party party) {
		if (party == null) {
			return null;
		}
		return new PartyType()
			.withName(party.getName())
			.withTitle(party.getTitle())
			.withOrganization(party.getOrganization())
			.withPersonalNumber(party.getPersonalNumber())
			.withEmailAddress(party.getEmail())
			.withMobilePhoneNumber(party.getPhoneNumber())
			.withPartyId(party.getPartyId())
			.withLanguage(party.getLanguage());

	}

	static List<DocumentType> toDocumentTypeList(final List<Document> documents) {

		if (documents == null) {
			return emptyList();
		}
		return documents.stream().map(SigningMapper::toDocumentType).toList();
	}

	static DocumentType toDocumentType(final Document document) {
		if (document == null) {
			return null;
		}
		return new DocumentType()
			.withDocumentName(document.getName())
			.withFileName(document.getFileName())
			.withMimeType(document.getMimeType())
			.withContent(Optional.ofNullable(document.getContent())
				.map(content -> content.getBytes(StandardCharsets.UTF_8)).orElse(null));
	}

	static XMLGregorianCalendar toXMLGregorianCalendar(final OffsetDateTime offsetDateTime) {
		if (offsetDateTime == null) {
			return null;
		}
		final var zonedDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault());
		final var gregorianCalendar = GregorianCalendar.from(zonedDateTime);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		} catch (final DatatypeConfigurationException e) {
			throw Problem.valueOf(org.zalando.problem.Status.INTERNAL_SERVER_ERROR, "Failed to convert OffsetDateTime to XMLGregorianCalendar");
		}
	}

	static OffsetDateTime toOffsetDateTime(final XMLGregorianCalendar xmlGregorianCalendar) {
		if (xmlGregorianCalendar == null) {
			return null;
		}
		return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toOffsetDateTime();
	}

	static Document toDocument(final DocumentType documentType) {
		if (documentType == null) {
			return null;
		}
		return Document.builder()
			.withName(documentType.getDocumentName())
			.withFileName(documentType.getFileName())
			.withMimeType(documentType.getMimeType())
			.withContent(new String(Optional.ofNullable(documentType.getContent())
				.orElse(new byte[0]), StandardCharsets.UTF_8))
			.build();
	}

	static Status toStatus(final comfact.Status status) {
		return Status.builder()
			.withCode(status.getStatusCode())
			.withMessage(status.getStatusMessage())
			.build();
	}


}
