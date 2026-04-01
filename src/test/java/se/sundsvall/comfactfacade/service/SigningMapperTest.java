package se.sundsvall.comfactfacade.service;

import generated.se.sundsvall.comfact.Message;
import generated.se.sundsvall.comfact.Paginator;
import generated.se.sundsvall.comfact.Property;
import generated.se.sundsvall.comfact.SearchResult;
import generated.se.sundsvall.comfact.Workflow;
import java.util.List;
import org.jose4j.base64url.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Identification;
import se.sundsvall.comfactfacade.api.model.NotificationMessage;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.Reminder;
import se.sundsvall.comfactfacade.api.model.SigningRequest;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SigningMapperTest {

	@Test
	void toSigningResponse() {
		// Arrange
		final var offsetDateTime = now();
		final var status = generated.se.sundsvall.comfact.Status.ACTIVE;
		final var customerReferenceNumber = "customerReferenceNumber";
		final var document = new generated.se.sundsvall.comfact.Document();
		final var initiator = new generated.se.sundsvall.comfact.Initiator();
		final var additionalParties = List.of(new generated.se.sundsvall.comfact.Party());
		final var signatories = List.of(new generated.se.sundsvall.comfact.Signatory());
		final var additionalDocuments = List.of(new generated.se.sundsvall.comfact.Document());
		final var signingId = "signingId";
		final var notificationMessages = new Message();
		final var signedDocument = new generated.se.sundsvall.comfact.Document();

		final var signingInstance = new generated.se.sundsvall.comfact.SigningInstance()
			.signingInstanceId(signingId)
			.status(status)
			.customerReferenceNumber(customerReferenceNumber)
			.created(offsetDateTime)
			.changed(offsetDateTime)
			.expires(offsetDateTime)
			.document(document)
			.initiator(initiator)
			.additionalParties(additionalParties)
			.signatories(signatories)
			.documentAttachments(additionalDocuments)
			.notificationMessages(List.of(notificationMessages))
			.signedDocument(signedDocument);

		// Act
		final var result = SigningMapper.toSigningResponse(signingInstance);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getStatus()).isNotNull();
		assertThat(result.getStatus().getCode()).isEqualTo("active");
		assertThat(result.getCustomerReference()).isEqualTo(customerReferenceNumber);
		assertThat(result.getCreated()).isCloseTo(now(), within(1, SECONDS));
		assertThat(result.getChanged()).isCloseTo(now(), within(1, SECONDS));
		assertThat(result.getExpires()).isCloseTo(now(), within(1, SECONDS));
		assertThat(result.getDocument()).isNotNull();
		assertThat(result.getInitiator()).isNotNull();
		assertThat(result.getAdditionalParties()).isNotNull().hasSize(1);
		assertThat(result.getSignatories()).isNotNull().hasSize(1);
		assertThat(result.getAdditionalDocuments()).isNotNull().hasSize(1);
		assertThat(result.getSigningId()).isEqualTo(signingId);
		assertThat(result.getNotificationMessages()).isNotNull().hasSize(1);
		assertThat(result.getSignedDocument()).isNotNull();
	}

	@Test
	void toNotificationMessage() {
		// Arrange
		final var subject = "subject";
		final var body = "body";
		final var language = "language";

		final var message = new Message()
			.subject(subject)
			.body(body)
			.language(language);

		// Act
		final var result = SigningMapper.toNotificationMessage(message);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getSubject()).isEqualTo(subject);
		assertThat(result.getBody()).isEqualTo(body);
		assertThat(result.getLanguage()).isEqualTo(language);
	}

	@Test
	void toParty() {
		// Arrange
		final var name = "name";
		final var title = "title";
		final var organization = "organization";
		final var emailAddress = "emailAddress";
		final var mobilePhoneNumber = "mobilePhoneNumber";
		final var partyId = "partyId";
		final var language = "language";

		final var party = new generated.se.sundsvall.comfact.Party()
			.name(name)
			.title(title)
			.organization(organization)
			.emailAddress(emailAddress)
			.mobilePhoneNumber(mobilePhoneNumber)
			.partyId(partyId)
			.language(language);

		// Act
		final var result = SigningMapper.toParty(party);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("notificationMessage", "identifications");
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getEmail()).isEqualTo(emailAddress);
		assertThat(result.getPhoneNumber()).isEqualTo(mobilePhoneNumber);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getLanguage()).isEqualTo(language);
	}

	@Test
	void toPartyFromInitiator() {
		// Arrange
		final var name = "name";
		final var title = "title";
		final var organization = "organization";
		final var emailAddress = "emailAddress";
		final var mobilePhoneNumber = "mobilePhoneNumber";
		final var partyId = "partyId";
		final var language = "language";

		final var initiator = new generated.se.sundsvall.comfact.Initiator()
			.name(name)
			.title(title)
			.organization(organization)
			.emailAddress(emailAddress)
			.mobilePhoneNumber(mobilePhoneNumber)
			.partyId(partyId)
			.language(language);

		// Act
		final var result = SigningMapper.toParty(initiator);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("notificationMessage", "identifications");
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getEmail()).isEqualTo(emailAddress);
		assertThat(result.getPhoneNumber()).isEqualTo(mobilePhoneNumber);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getLanguage()).isEqualTo(language);
	}

	@Test
	void toSigningInstanceInfoType() {
		// Arrange
		final var offsetDateTime = now();
		final var status = generated.se.sundsvall.comfact.Status.CREATED;
		final var customerReferenceNumber = "customerReferenceNumber";
		final var signingId = "signingId";

		final var signingInstanceInfo = new generated.se.sundsvall.comfact.SigningInstanceInfo()
			.status(status)
			.customerReferenceNumber(customerReferenceNumber)
			.created(offsetDateTime)
			.changed(offsetDateTime)
			.expires(offsetDateTime)
			.signingInstanceId(signingId);

		// Act
		final var result = SigningMapper.toSigningInstanceInfoType(signingInstanceInfo);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("notificationMessages",
			"initiator",
			"additionalParties",
			"signatories",
			"document",
			"signedDocument",
			"additionalDocuments");
		assertThat(result.getCustomerReference()).isEqualTo(customerReferenceNumber);
		assertThat(result.getCreated()).isCloseTo(now(), within(1, SECONDS));
		assertThat(result.getChanged()).isCloseTo(now(), within(1, SECONDS));
		assertThat(result.getExpires()).isCloseTo(now(), within(1, SECONDS));
		assertThat(result.getSigningId()).isEqualTo(signingId);
		assertThat(result.getStatus()).isNotNull();
	}

	@Test
	void toSigningInstancePatch() {
		// Arrange
		final var signingId = "signingId";
		final var signingRequest = SigningRequest.builder()
			.withExpires(now())
			.build();

		// Act
		final var result = SigningMapper.toSigningInstancePatch(signingRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getExpires()).isCloseTo(now(), within(1, SECONDS));
	}

	@Test
	void toSigningInstanceInput() {
		// Arrange
		final var customerReference = "customerReference";
		final var expires = now();
		final var notificationMessage = NotificationMessage.builder().build();
		final var reminder = Reminder.builder().build();
		final var initiator = Party.builder().build();
		final var additionalParty = Party.builder().build();
		final var additionalParties = List.of(additionalParty);
		final var signatory = se.sundsvall.comfactfacade.api.model.Signatory.builder().build();
		final var signatories = List.of(signatory);
		final var document = Document.builder().build();
		final var language = "language";
		final var additionalDocuments = List.of(Document.builder().build());
		final var flowType = "Parallel";
		final var signingRequest = SigningRequest.builder()
			.withInitiator(initiator)
			.withAdditionalParties(additionalParties)
			.withSignatories(signatories)
			.withDocument(document)
			.withLanguage(language)
			.withCustomerReference(customerReference)
			.withExpires(expires)
			.withNotificationMessage(notificationMessage)
			.withReminder(reminder)
			.withAdditionalDocuments(additionalDocuments)
			.withFlowType(flowType)
			.build();

		// Act
		final var result = SigningMapper.toSigningInstanceInput(signingRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getCustomerReferenceNumber()).isEqualTo(customerReference);
		assertThat(result.getExpires()).isCloseTo(expires, within(1, SECONDS));
		assertThat(result.getNotificationMessages()).isNotNull().hasSize(1);
		assertThat(result.getAutomaticReminder()).isNotNull();
		assertThat(result.getInitiator()).isNotNull();
		assertThat(result.getAdditionalParties()).isNotNull().hasSize(1);
		assertThat(result.getSignatories()).isNotNull().hasSize(1);
		assertThat(result.getDocument()).isNotNull();
		assertThat(result.getLanguage()).isEqualTo(language);
		assertThat(result.getDocumentAttachments()).isNotNull().hasSize(1);
		assertThat(result.getWorkflow()).isEqualTo(Workflow.PARALLEL);
	}

	@Test
	void toComfactSignatory() {
		// Arrange
		final var subject = "subject";
		final var body = "body";
		final var name = "name";
		final var title = "title";
		final var organization = "organization";
		final var emailAddress = "emailAddress";
		final var mobilePhoneNumber = "mobilePhoneNumber";
		final var partyId = "partyId";
		final var language = "language";
		final var identifications = List.of(new Identification("SmsCode"));
		final var notificationMessage = NotificationMessage.builder()
			.withSubject(subject)
			.withBody(body)
			.withLanguage(language)
			.build();
		final var signatory = se.sundsvall.comfactfacade.api.model.Signatory.builder()
			.withName(name)
			.withTitle(title)
			.withOrganization(organization)
			.withEmail(emailAddress)
			.withPhoneNumber(mobilePhoneNumber)
			.withPartyId(partyId)
			.withLanguage(language)
			.withIdentifications(identifications)
			.withNotificationMessage(notificationMessage)
			.build();

		// Act
		final var result = SigningMapper.toComfactSignatory(signatory);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getEmailAddress()).isEqualTo(emailAddress);
		assertThat(result.getMobilePhoneNumber()).isEqualTo(mobilePhoneNumber);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getLanguage()).isEqualTo(language);
		assertThat(result.getAuthenticationMethods()).isNotNull().hasSize(1).containsExactly("SmsCode");
		assertThat(result.getNotificationMessage()).isNotNull();
		assertThat(result.getNotificationMessage().getSubject()).isEqualTo(subject);
		assertThat(result.getNotificationMessage().getBody()).isEqualTo(body);
		assertThat(result.getNotificationMessage().getLanguage()).isEqualTo(language);
	}

	@Test
	void toMessage() {
		// Arrange
		final var subject = "subject";
		final var body = "body";
		final var language = "language";
		final var notificationMessage = NotificationMessage.builder()
			.withSubject(subject)
			.withBody(body)
			.withLanguage(language)
			.build();

		// Act
		final var result = SigningMapper.toMessage(notificationMessage);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getSubject()).isEqualTo(subject);
		assertThat(result.getBody()).isEqualTo(body);
		assertThat(result.getLanguage()).isEqualTo(language);
	}

	@Test
	void toAutomaticReminder() {
		// Arrange
		final var subject = "subject";
		final var body = "body";
		final var language = "language";
		final var enabled = true;
		final var intervalInHours = 1;
		final var startDateTime = now();

		final var notificationMessage = NotificationMessage.builder()
			.withSubject(subject)
			.withBody(body)
			.withLanguage(language)
			.build();

		final var reminder = Reminder.builder()
			.withMessage(notificationMessage)
			.withEnabled(enabled)
			.withIntervalInHours(intervalInHours)
			.withStartDateTime(startDateTime)
			.build();

		// Act
		final var result = SigningMapper.toAutomaticReminder(reminder);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getReminderMessages()).isNotNull().hasSize(1);
		assertThat(result.getReminderMessages().getFirst().getSubject()).isEqualTo(subject);
		assertThat(result.getReminderMessages().getFirst().getBody()).isEqualTo(body);
		assertThat(result.getReminderMessages().getFirst().getLanguage()).isEqualTo(language);
		assertThat(result.getEnabled()).isEqualTo(enabled);
		assertThat(result.getHourInterval()).isEqualTo(intervalInHours);
		assertThat(result.getStartDate()).isCloseTo(startDateTime, within(1, SECONDS));
	}

	@Test
	void toComfactParty() {
		// Arrange
		final var name = "name";
		final var title = "title";
		final var organization = "organization";
		final var emailAddress = "emailAddress";
		final var mobilePhoneNumber = "mobilePhoneNumber";
		final var partyId = "partyId";
		final var language = "language";

		final var party = Party.builder()
			.withName(name)
			.withTitle(title)
			.withOrganization(organization)
			.withEmail(emailAddress)
			.withPhoneNumber(mobilePhoneNumber)
			.withPartyId(partyId)
			.withLanguage(language)
			.build();

		// Act
		final var result = SigningMapper.toComfactParty(party);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getEmailAddress()).isEqualTo(emailAddress);
		assertThat(result.getMobilePhoneNumber()).isEqualTo(mobilePhoneNumber);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getLanguage()).isEqualTo(language);
	}

	@Test
	void toComfactDocumentList() {
		// Arrange
		final var documentName = "documentName";
		final var fileName = "fileName";
		final var mimeType = "mimeType";
		final var content = "content";

		final var document = Document.builder()
			.withName(documentName)
			.withFileName(fileName)
			.withMimeType(mimeType)
			.withContent(content)
			.build();

		// Act
		final var result = SigningMapper.toComfactDocumentList(List.of(document));

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getDocumentName()).isEqualTo(documentName);
		assertThat(result.getFirst().getFileName()).isEqualTo(fileName);
		assertThat(result.getFirst().getMimeType()).isEqualTo(mimeType);
		assertThat(result.getFirst().getContent()).isEqualTo(Base64.decode(content));
	}

	@Test
	void toComfactDocument() {
		// Arrange
		final var documentName = "documentName";
		final var fileName = "fileName";
		final var mimeType = "mimeType";
		final var content = "content";

		final var document = Document.builder()
			.withName(documentName)
			.withFileName(fileName)
			.withMimeType(mimeType)
			.withContent(content)
			.build();

		// Act
		final var result = SigningMapper.toComfactDocument(document);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDocumentName()).isEqualTo(documentName);
		assertThat(result.getFileName()).isEqualTo(fileName);
		assertThat(result.getMimeType()).isEqualTo(mimeType);
		assertThat(result.getContent()).isEqualTo(Base64.decode(content));
	}

	@Test
	void toDocument() {
		// Arrange
		final var documentName = "documentName";
		final var fileName = "fileName";
		final var mimeType = "mimeType";
		final var content = "content".getBytes();

		final var document = new generated.se.sundsvall.comfact.Document()
			.documentName(documentName)
			.fileName(fileName)
			.mimeType(mimeType)
			.content(content);

		// Act
		final var result = SigningMapper.toDocument(document);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getName()).isEqualTo(documentName);
		assertThat(result.getFileName()).isEqualTo(fileName);
		assertThat(result.getMimeType()).isEqualTo(mimeType);
		assertThat(result.getContent()).isNotNull();
		assertThat(isValidBase64(result.getContent())).isTrue();
	}

	@Test
	void toStatus() {
		// Arrange
		final var status = generated.se.sundsvall.comfact.Status.ACTIVE;

		// Act
		final var result = SigningMapper.toStatus(status);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo("active");
	}

	@Test
	void toStatus_null() {
		assertThat(SigningMapper.toStatus(null)).isNull();
	}

	@Test
	void toSearchFilter() {
		// Arrange
		final var pageable = PageRequest.of(0, 10).withSort(Sort.by(Sort.Order.desc("created")));

		// Act
		final var result = SigningMapper.toSearchFilter(pageable);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getPaginator()).isNotNull();
		assertThat(result.getPaginator().getPage()).isZero();
		assertThat(result.getPaginator().getPageSize()).isEqualTo(10);
		assertThat(result.getPaginator().getOrderByDescending()).isTrue();
		assertThat(result.getPaginator().getOrderByProperty()).isEqualTo(Property.CREATED);
	}

	@Test
	void toSigningsResponse() {
		// Arrange
		final var page = 0;
		final var pageSize = 10;
		final var totalItems = 20;
		final var totalPages = totalItems / pageSize;
		final var signingId = "123";

		final var paginator = new Paginator()
			.page(page)
			.pageSize(pageSize)
			.orderByDescending(true)
			.totalItems(totalItems)
			.orderByProperty(Property.CREATED);

		final var signingInstanceInfo = new generated.se.sundsvall.comfact.SigningInstanceInfo()
			.signingInstanceId(signingId)
			.status(generated.se.sundsvall.comfact.Status.ACTIVE);

		final var searchResult = new SearchResult()
			.signingInstanceInfos(List.of(signingInstanceInfo))
			.paginator(paginator);

		// Act
		final var result = SigningMapper.toSigningsResponse(searchResult);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getSigningInstances()).hasSize(1);
		assertThat(result.getSigningInstances().getFirst().getSigningId()).isEqualTo(signingId);

		assertThat(result.getPagingAndSortingMetaData().getPage()).isEqualTo(page);
		assertThat(result.getPagingAndSortingMetaData().getCount()).isEqualTo(1);
		assertThat(result.getPagingAndSortingMetaData().getLimit()).isEqualTo(pageSize);
		assertThat(result.getPagingAndSortingMetaData().getTotalRecords()).isEqualTo(totalItems);
		assertThat(result.getPagingAndSortingMetaData().getTotalPages()).isEqualTo(totalPages);
	}

	@Test
	void toSigningsResponse_noPaging() {
		// Arrange
		final var signingId = "123";
		final var signingInstanceInfo = new generated.se.sundsvall.comfact.SigningInstanceInfo()
			.signingInstanceId(signingId)
			.status(generated.se.sundsvall.comfact.Status.ACTIVE);

		final var searchResult = new SearchResult()
			.signingInstanceInfos(List.of(signingInstanceInfo));

		// Act
		final var result = SigningMapper.toSigningsResponse(searchResult);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("pagingAndSortingMetaData");
		assertThat(result.getSigningInstances()).hasSize(1);
		assertThat(result.getSigningInstances().getFirst().getSigningId()).isEqualTo(signingId);
		assertThat(result.getPagingAndSortingMetaData()).isNull();
	}

	@Test
	void toWorkflow_null() {
		assertThat(SigningMapper.toWorkflow(null)).isEqualTo(Workflow.SEQUENTIAL);
	}

	@Test
	void toWorkflow_sequential() {
		assertThat(SigningMapper.toWorkflow("Sequential")).isEqualTo(Workflow.SEQUENTIAL);
	}

	@Test
	void toWorkflow_parallel() {
		assertThat(SigningMapper.toWorkflow("Parallel")).isEqualTo(Workflow.PARALLEL);
	}

	@Test
	void toWorkflow_caseInsensitive() {
		assertThat(SigningMapper.toWorkflow("parallel")).isEqualTo(Workflow.PARALLEL);
		assertThat(SigningMapper.toWorkflow("PARALLEL")).isEqualTo(Workflow.PARALLEL);
		assertThat(SigningMapper.toWorkflow("pARALLEL")).isEqualTo(Workflow.PARALLEL);
		assertThat(SigningMapper.toWorkflow("sequential")).isEqualTo(Workflow.SEQUENTIAL);
		assertThat(SigningMapper.toWorkflow("SEQUENTIAL")).isEqualTo(Workflow.SEQUENTIAL);
		assertThat(SigningMapper.toWorkflow("sEQUENTIAL")).isEqualTo(Workflow.SEQUENTIAL);
	}

	private boolean isValidBase64(final String s) {
		try {
			Base64.decode(s);
			return true;
		} catch (final IllegalArgumentException e) {
			return false;
		}
	}
}
