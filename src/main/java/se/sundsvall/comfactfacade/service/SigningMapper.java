package se.sundsvall.comfactfacade.service;

import static java.util.Collections.emptyList;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jose4j.base64url.Base64;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.w3c.dom.Element;
import org.zalando.problem.Problem;

import comfact.CreateSigningInstanceRequest;
import comfact.Custom;
import comfact.DocumentType;
import comfact.GetSigningInstanceInfoRequest;
import comfact.GetSigningInstanceInfoResponse;
import comfact.GetSigningInstanceResponse;
import comfact.MessageType;
import comfact.Paginator;
import comfact.PartyType;
import comfact.SignatoryReminder;
import comfact.SigningInstanceInfo;
import comfact.SigningInstanceInputType;
import comfact.UpdateSigningInstanceRequest;
import comfact.WithdrawSigningInstanceRequest;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Identification;
import se.sundsvall.comfactfacade.api.model.NotificationMessage;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.Reminder;
import se.sundsvall.comfactfacade.api.model.Signatory;
import se.sundsvall.comfactfacade.api.model.SigningInstance;
import se.sundsvall.comfactfacade.api.model.SigningRequest;
import se.sundsvall.comfactfacade.api.model.SigningsResponse;
import se.sundsvall.comfactfacade.api.model.Status;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

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
			.withSignatories(response.getSigningInstance().getSignatories().stream().map(SigningMapper::toSignatory).toList())
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

		return Party.builder()
			.withEmail(partyType.getEmailAddress())
			.withName(partyType.getName())
			.withOrganization(partyType.getOrganization())
			.withPartyId(partyType.getPartyId())
			.withPhoneNumber(partyType.getMobilePhoneNumber())
			.withTitle(partyType.getTitle())
			.withLanguage(partyType.getLanguage())
			.build();
	}

	static Signatory toSignatory(final comfact.Signatory signatory) {
		if (signatory == null) {
			return null;
		}
		return Signatory.builder()
			.withEmail(signatory.getEmailAddress())
			.withName(signatory.getName())
			.withOrganization(signatory.getOrganization())
			.withPartyId(signatory.getPartyId())
			.withPhoneNumber(signatory.getMobilePhoneNumber())
			.withTitle(signatory.getTitle())
			.withLanguage(signatory.getLanguage())
			.withIdentifications(signatory.getIdentifications().stream().map(identification -> new Identification(identification.getAlias())).toList())
			.withNotificationMessage(toNotificationMessage(signatory.getNotificationMessage()))
			.build();
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
			.withSigningInstanceInput(toSigningInstanceInputType(signingRequest));
	}

	static SigningInstanceInputType toSigningInstanceInputType(final SigningRequest signingRequest) {
		return new SigningInstanceInputType()
			.withCustomerReferenceNumber(signingRequest.getCustomerReference())
			.withExpires(toXMLGregorianCalendar(signingRequest.getExpires()))
			.withNotificationMessages(toMessageType(signingRequest.getNotificationMessage()))
			.withSignatoryReminder(toSignatoryReminderType(signingRequest.getReminder()))
			.withInitiator(toPartyType(signingRequest.getInitiator()))
			.withAdditionalParties(Optional.ofNullable(signingRequest.getAdditionalParties()).stream().flatMap(List::stream).map(SigningMapper::toPartyType).toList())
			.withSignatories(Optional.ofNullable(signingRequest.getSignatories()).stream().flatMap(List::stream).map(SigningMapper::toSignatoryType).toList())
			.withLanguage(signingRequest.getLanguage())
			.withDocument(toDocumentType(signingRequest.getDocument()))
			.withDocumentAttachments(toDocumentTypeList(signingRequest.getAdditionalDocuments()));
	}

	static CreateSigningInstanceRequest toCreateSigningInstanceRequestType(final SigningRequest signingRequest) {
		return new CreateSigningInstanceRequest().withSigningInstanceInput(toSigningInstanceInputType(signingRequest));
	}

	static comfact.Signatory toSignatoryType(final Signatory signatory) {
		if (signatory == null) {
			return null;
		}
		return new comfact.Signatory()
			.withName(signatory.getName())
			.withTitle(signatory.getTitle())
			.withOrganization(signatory.getOrganization())
			.withEmailAddress(signatory.getEmail())
			.withMobilePhoneNumber(signatory.getPhoneNumber())
			.withPartyId(signatory.getPartyId())
			.withNotificationMessage(toMessageType(signatory.getNotificationMessage()))
			.withIdentifications(
				Optional.ofNullable(signatory.getIdentifications()).stream().flatMap(List::stream).map(identification -> new comfact.Identification().withAlias(identification.getAlias())).toList())
			.withLanguage(signatory.getLanguage());
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
			.withContent(Base64.decode(document.getContent()));
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
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "Failed to convert OffsetDateTime to XMLGregorianCalendar");
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
			.withContent(Base64.encode(Optional.ofNullable(documentType.getContent())
				.orElse(new byte[0])))
			.build();
	}

	static Status toStatus(final comfact.Status status) {
		return Status.builder()
			.withCode(status.getStatusCode())
			.withMessage(status.getStatusMessage())
			.build();
	}

	static GetSigningInstanceInfoRequest toGetSigningInstanceInfoRequest(final Pageable pageable) {

		final var paginator = toPaginator(pageable);
		final var document = toDocument(paginator);

		return new GetSigningInstanceInfoRequest()
			.withCustom(new Custom()
				.withAnies(document));
	}

	static Element toDocument(final Paginator paginator) {
		try {
			final var document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();

			JAXBContext.newInstance(Paginator.class.getPackage().getName())
				.createMarshaller()
				.marshal(paginator, document);
			return document.getDocumentElement();
		} catch (final ParserConfigurationException | JAXBException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "Failed to create document element");
		}
	}

	private static Paginator toPaginator(final Pageable pageable) {
		// Parse the first sort order as the API only supports one sort order
		final var firstSortOrder = pageable.getSort().stream()
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, "Sort order is required"));

		return new Paginator()
			.withPage(pageable.getPageNumber())
			.withPageSize(pageable.getPageSize())
			.withOrderByDescending(firstSortOrder.isDescending())
			.withOrderByProperty(firstSortOrder.getProperty());

	}

	static SigningsResponse toSigningsResponse(final GetSigningInstanceInfoResponse request) {
		return SigningsResponse.builder()
			.withSigningInstances(request.getSigningInstanceInfos().stream().map(SigningMapper::toSigningInstanceInfoType).toList())
			.withPagingAndSortingMetaData(toPagingAndSortingMetaData(request.getCustom(), request.getSigningInstanceInfos().size()))
			.build();
	}

	private static PagingAndSortingMetaData toPagingAndSortingMetaData(final Custom custom, final int size) {

		return Optional.ofNullable(toPaginator(custom))
			.map(paginator -> new PagingAndSortingMetaData()
				.withPage(paginator.getPage())
				.withCount(size)
				.withLimit(paginator.getPageSize())
				.withTotalRecords(Optional.ofNullable(paginator.getTotalItems()).orElse(0))
				.withTotalPages(calculateTotalPages(paginator))
				.withSortBy(List.of(paginator.getOrderByProperty()))
				.withSortDirection(paginator.isOrderByDescending() ? Sort.Direction.DESC : Sort.Direction.ASC))
			.orElse(null);

	}

	private static Paginator toPaginator(final Custom custom) {

		if (custom == null) {
			return null;
		}

		for (final var item : Optional.ofNullable(custom.getAnies()).orElse(emptyList())) {
			final var jaxbElement = SigningMapper.getJaxbElement(item);
			if (jaxbElement == null) {
				continue;
			}
			return jaxbElement.getValue();
		}
		return null;
	}

	private static JAXBElement<Paginator> getJaxbElement(final Element element) {
		try {
			return JAXBContext.newInstance(Paginator.class.getPackage().getName())
				.createUnmarshaller()
				.unmarshal(element, Paginator.class);
		} catch (final Exception e) {
			return null;
		}
	}

	private static int calculateTotalPages(final Paginator paginator) {
		return Optional.ofNullable(paginator.getTotalItems())
			.map(totalItems -> (int) Math.ceil((double) totalItems / paginator.getPageSize()))
			.orElse(0);
	}
}
