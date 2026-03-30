package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.AutomaticReminder;
import generated.se.sundsvall.comfact.Initiator;
import generated.se.sundsvall.comfact.Message;
import generated.se.sundsvall.comfact.Paginator;
import generated.se.sundsvall.comfact.Property;
import generated.se.sundsvall.comfact.SearchFilter;
import generated.se.sundsvall.comfact.SearchResult;
import generated.se.sundsvall.comfact.SigningInstanceInput;
import generated.se.sundsvall.comfact.SigningInstancePatch;
import generated.se.sundsvall.comfact.Workflow;
import java.util.List;
import java.util.Optional;
import org.jose4j.base64url.Base64;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import se.sundsvall.dept44.problem.Problem;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public final class SigningMapper {

	private SigningMapper() {
		// Intentionally left empty
	}

	static SigningInstance toSigningResponse(final generated.se.sundsvall.comfact.SigningInstance response) {
		return SigningInstance.builder()
			.withStatus(toStatus(response.getStatus()))
			.withCustomerReference(response.getCustomerReferenceNumber())
			.withCreated(response.getCreated())
			.withChanged(response.getChanged())
			.withDocument(toDocument(response.getDocument()))
			.withInitiator(toParty(response.getInitiator()))
			.withSignatories(response.getSignatories().stream().map(SigningMapper::toSignatory).toList())
			.withAdditionalParties(response.getAdditionalParties().stream().map(SigningMapper::toParty).toList())
			.withAdditionalDocuments(response.getDocumentAttachments().stream().map(SigningMapper::toDocument).toList())
			.withSigningId(response.getSigningInstanceId())
			.withExpires(response.getExpires())
			.withNotificationMessages(response.getNotificationMessages().stream().map(SigningMapper::toNotificationMessage).toList())
			.withSignedDocument(toDocument(response.getSignedDocument()))
			.build();
	}

	static NotificationMessage toNotificationMessage(final Message message) {
		if (message == null) {
			return null;
		}
		return NotificationMessage.builder()
			.withSubject(message.getSubject())
			.withBody(message.getBody())
			.withLanguage(message.getLanguage())
			.build();
	}

	static Party toParty(final generated.se.sundsvall.comfact.Party party) {
		if (party == null) {
			return null;
		}
		return Party.builder()
			.withEmail(party.getEmailAddress())
			.withName(party.getName())
			.withOrganization(party.getOrganization())
			.withPartyId(party.getPartyId())
			.withPhoneNumber(party.getMobilePhoneNumber())
			.withTitle(party.getTitle())
			.withLanguage(party.getLanguage())
			.build();
	}

	static Party toParty(final Initiator initiator) {
		if (initiator == null) {
			return null;
		}
		return Party.builder()
			.withEmail(initiator.getEmailAddress())
			.withName(initiator.getName())
			.withOrganization(initiator.getOrganization())
			.withPartyId(initiator.getPartyId())
			.withPhoneNumber(initiator.getMobilePhoneNumber())
			.withTitle(initiator.getTitle())
			.withLanguage(initiator.getLanguage())
			.build();
	}

	static Signatory toSignatory(final generated.se.sundsvall.comfact.Signatory signatory) {
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
			.withIdentifications(signatory.getAuthenticationMethods().stream().map(Identification::new).toList())
			.withNotificationMessage(toNotificationMessage(signatory.getNotificationMessage()))
			.build();
	}

	static SigningInstance toSigningInstanceInfoType(final generated.se.sundsvall.comfact.SigningInstanceInfo response) {
		return SigningInstance.builder()
			.withStatus(toStatus(response.getStatus()))
			.withCustomerReference(response.getCustomerReferenceNumber())
			.withCreated(response.getCreated())
			.withChanged(response.getChanged())
			.withExpires(response.getExpires())
			.withSigningId(response.getSigningInstanceId())
			.build();
	}

	static SigningInstancePatch toSigningInstancePatch(final String signingId, final SigningRequest signingRequest) {
		return new SigningInstancePatch()
			.expires(signingRequest.getExpires());
	}

	static SigningInstanceInput toSigningInstanceInput(final SigningRequest signingRequest) {
		return new SigningInstanceInput()
			.customerReferenceNumber(signingRequest.getCustomerReference())
			.expires(signingRequest.getExpires())
			.notificationMessages(toMessageList(signingRequest.getNotificationMessage()))
			.automaticReminder(toAutomaticReminder(signingRequest.getReminder()))
			.initiator(toInitiator(signingRequest.getInitiator()))
			.additionalParties(Optional.ofNullable(signingRequest.getAdditionalParties()).stream().flatMap(List::stream).map(SigningMapper::toComfactParty).toList())
			.signatories(Optional.ofNullable(signingRequest.getSignatories()).stream().flatMap(List::stream).map(SigningMapper::toComfactSignatory).toList())
			.language(signingRequest.getLanguage())
			.document(toComfactDocument(signingRequest.getDocument()))
			.workflow(toWorkflow(signingRequest.getFlowType()))
			.documentAttachments(toComfactDocumentList(signingRequest.getAdditionalDocuments()));
	}

	static Workflow toWorkflow(final String flowType) {
		return Optional.ofNullable(flowType)
			.map(String::toLowerCase)
			.map(Workflow::fromValue)
			.orElse(Workflow.SEQUENTIAL);
	}

	static generated.se.sundsvall.comfact.Signatory toComfactSignatory(final Signatory signatory) {
		if (signatory == null) {
			return null;
		}
		return new generated.se.sundsvall.comfact.Signatory()
			.name(signatory.getName())
			.title(signatory.getTitle())
			.organization(signatory.getOrganization())
			.emailAddress(signatory.getEmail())
			.mobilePhoneNumber(signatory.getPhoneNumber())
			.partyId(signatory.getPartyId())
			.notificationMessage(toMessage(signatory.getNotificationMessage()))
			.authenticationMethods(
				Optional.ofNullable(signatory.getIdentifications()).stream().flatMap(List::stream).map(Identification::getAlias).toList())
			.language(signatory.getLanguage());
	}

	static List<Message> toMessageList(final NotificationMessage notificationMessage) {
		if (notificationMessage == null) {
			return emptyList();
		}
		return List.of(toMessage(notificationMessage));
	}

	static Message toMessage(final NotificationMessage notificationMessage) {
		if (notificationMessage == null) {
			return null;
		}
		return new Message()
			.subject(notificationMessage.getSubject())
			.body(notificationMessage.getBody())
			.language(notificationMessage.getLanguage());
	}

	static AutomaticReminder toAutomaticReminder(final Reminder reminder) {
		if (reminder == null) {
			return null;
		}
		return new AutomaticReminder()
			.reminderMessages(toMessageList(reminder.getMessage()))
			.enabled(reminder.isEnabled())
			.hourInterval(reminder.getIntervalInHours())
			.startDate(reminder.getStartDateTime());
	}

	static Initiator toInitiator(final Party party) {
		if (party == null) {
			return null;
		}
		return new Initiator()
			.name(party.getName())
			.title(party.getTitle())
			.organization(party.getOrganization())
			.emailAddress(party.getEmail())
			.mobilePhoneNumber(party.getPhoneNumber())
			.partyId(party.getPartyId())
			.language(party.getLanguage());
	}

	static generated.se.sundsvall.comfact.Party toComfactParty(final Party party) {
		if (party == null) {
			return null;
		}
		return new generated.se.sundsvall.comfact.Party()
			.name(party.getName())
			.title(party.getTitle())
			.organization(party.getOrganization())
			.emailAddress(party.getEmail())
			.mobilePhoneNumber(party.getPhoneNumber())
			.partyId(party.getPartyId())
			.language(party.getLanguage());
	}

	static List<generated.se.sundsvall.comfact.Document> toComfactDocumentList(final List<Document> documents) {
		if (documents == null) {
			return emptyList();
		}
		return documents.stream().map(SigningMapper::toComfactDocument).toList();
	}

	static generated.se.sundsvall.comfact.Document toComfactDocument(final Document document) {
		if (document == null) {
			return null;
		}
		return new generated.se.sundsvall.comfact.Document()
			.documentName(document.getName())
			.fileName(document.getFileName())
			.mimeType(document.getMimeType())
			.content(Base64.decode(document.getContent()));
	}

	static Document toDocument(final generated.se.sundsvall.comfact.Document document) {
		if (document == null) {
			return null;
		}
		return Document.builder()
			.withName(document.getDocumentName())
			.withFileName(document.getFileName())
			.withMimeType(document.getMimeType())
			.withContent(Base64.encode(Optional.ofNullable(document.getContent())
				.orElse(new byte[0])))
			.build();
	}

	static Status toStatus(final generated.se.sundsvall.comfact.Status status) {
		if (status == null) {
			return null;
		}
		return Status.builder()
			.withCode(status.getValue())
			.build();
	}

	static SearchFilter toSearchFilter(final Pageable pageable) {
		return new SearchFilter()
			.paginator(toPaginator(pageable));
	}

	private static Paginator toPaginator(final Pageable pageable) {
		final var firstSortOrder = pageable.getSort().stream()
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, "Sort order is required"));

		return new Paginator()
			.page(pageable.getPageNumber())
			.pageSize(pageable.getPageSize())
			.orderByDescending(firstSortOrder.isDescending())
			.orderByProperty(Property.fromValue(firstSortOrder.getProperty().substring(0, 1).toLowerCase() + firstSortOrder.getProperty().substring(1)));
	}

	static SigningsResponse toSigningsResponse(final SearchResult searchResult) {
		return SigningsResponse.builder()
			.withSigningInstances(searchResult.getSigningInstanceInfos().stream().map(SigningMapper::toSigningInstanceInfoType).toList())
			.withPagingAndSortingMetaData(toPagingAndSortingMetaData(searchResult.getPaginator(), searchResult.getSigningInstanceInfos().size()))
			.build();
	}

	private static PagingAndSortingMetaData toPagingAndSortingMetaData(final Paginator paginator, final int size) {
		return Optional.ofNullable(paginator)
			.map(p -> new PagingAndSortingMetaData()
				.withPage(p.getPage())
				.withCount(size)
				.withLimit(p.getPageSize())
				.withTotalRecords(Optional.ofNullable(p.getTotalItems()).orElse(0))
				.withTotalPages(calculateTotalPages(p))
				.withSortBy(List.of(p.getOrderByProperty().getValue()))
				.withSortDirection(p.getOrderByDescending() ? Sort.Direction.DESC : Sort.Direction.ASC))
			.orElse(null);
	}

	private static int calculateTotalPages(final Paginator paginator) {
		return Optional.ofNullable(paginator.getTotalItems())
			.map(totalItems -> (int) Math.ceil((double) totalItems / paginator.getPageSize()))
			.orElse(0);
	}
}
