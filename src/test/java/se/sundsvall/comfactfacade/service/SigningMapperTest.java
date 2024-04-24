package se.sundsvall.comfactfacade.service;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jose4j.base64url.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import comfact.Custom;
import comfact.DocumentType;
import comfact.GetSigningInstanceResponse;
import comfact.MessageType;
import comfact.Paginator;
import comfact.PartyType;
import comfact.Signatory;
import comfact.SigningInstance;
import comfact.SigningInstanceInfo;
import comfact.Status;
import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.Identification;
import se.sundsvall.comfactfacade.api.model.NotificationMessage;
import se.sundsvall.comfactfacade.api.model.Party;
import se.sundsvall.comfactfacade.api.model.Reminder;
import se.sundsvall.comfactfacade.api.model.SigningRequest;

class SigningMapperTest {


	@Test
	void toSigningResponse() throws DatatypeConfigurationException {
		// Arrange

		final var zonedDateTime = now().atZoneSameInstant(ZoneId.systemDefault());
		final var gregorianCalendar = GregorianCalendar.from(zonedDateTime);
		final var xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);


		final var status = new Status();
		final var customerReferenceNumber = "customerReferenceNumber";
		final var document = new DocumentType();
		final var initiator = new PartyType();
		final var additionalParties = List.of(new PartyType());
		final var signatories = List.of(new Signatory());
		final var additionalDocuments = List.of(new DocumentType());
		final var signingId = "signingId";
		final var notificationMessages = new MessageType();
		final var signedDocument = new DocumentType();

		final var getSigningInstanceResponse = new GetSigningInstanceResponse()
			.withSigningInstance(new SigningInstance()
				.withSigningInstanceId(signingId)
				.withStatus(status)
				.withCustomerReferenceNumber(customerReferenceNumber)
				.withCreated(xmlGregorianCalendar)
				.withChanged(xmlGregorianCalendar)
				.withExpires(xmlGregorianCalendar)
				.withDocument(document)
				.withInitiator(initiator)
				.withAdditionalParties(additionalParties)
				.withSignatories(signatories)
				.withDocumentAttachments(additionalDocuments)
				.withNotificationMessages(notificationMessages)
				.withSignedDocument(signedDocument));

		// Act
		final var result = SigningMapper.toSigningResponse(getSigningInstanceResponse);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getStatus()).isNotNull();
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

		final var messageType = new MessageType()
			.withSubject(subject)
			.withBody(body)
			.withLanguage(language);

		// Act
		final var result = SigningMapper.toNotificationMessage(messageType);

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
		final var personalNumber = "personalNumber";
		final var emailAddress = "emailAddress";
		final var mobilePhoneNumber = "mobilePhoneNumber";
		final var partyId = "partyId";
		final var language = "language";

		final var party = new PartyType()
			.withName(name)
			.withTitle(title)
			.withOrganization(organization)
			.withPersonalNumber(personalNumber)
			.withEmailAddress(emailAddress)
			.withMobilePhoneNumber(mobilePhoneNumber)
			.withPartyId(partyId)
			.withLanguage(language);

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
	void toSigningInstanceInfoType() throws DatatypeConfigurationException {
		// Arrange
		final var zonedDateTime = now().atZoneSameInstant(ZoneId.systemDefault());
		final var gregorianCalendar = GregorianCalendar.from(zonedDateTime);
		final var xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

		final var status = new Status();
		final var customerReferenceNumber = "customerReferenceNumber";
		final var signingId = "signingId";

		final var signingInstanceInfo = new SigningInstanceInfo()
			.withStatus(status)
			.withCustomerReferenceNumber(customerReferenceNumber)
			.withCreated(xmlGregorianCalendar)
			.withChanged(xmlGregorianCalendar)
			.withExpires(xmlGregorianCalendar)
			.withSigningInstanceId(signingId);

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
	void toWithdrawSigningInstanceRequestType() {
		// Arrange
		final var signingId = "signingId";
		// Act
		final var result = SigningMapper.toWithdrawSigningInstanceRequestType(signingId);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("credentials", "requestingSource", "custom", "requestId");
		assertThat(result.getSigningInstanceId()).isEqualTo(signingId);
	}

	@Test
	void toUpdateSigningInstanceRequestType() {
		// Arrange
		final var signingId = "signingId";
		final var signingRequest = SigningRequest.builder().build();
		// Act
		final var result = SigningMapper.toUpdateSigningInstanceRequestType(signingId, signingRequest);
		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("credentials", "requestingSource", "custom", "requestId");
		assertThat(result.getSigningInstanceId()).isEqualTo(signingId);
		assertThat(result.getSigningInstanceInput()).isNotNull();
	}

	@Test
	void toSigningInstanceInputType() {
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
			.build();

		// Act
		final var result = SigningMapper.toSigningInstanceInputType(signingRequest);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("custom");
		assertThat(result.getCustomerReferenceNumber()).isEqualTo(customerReference);
		assertThat(result.getExpires().toGregorianCalendar().toZonedDateTime()).isCloseTo(expires.toZonedDateTime(), within(1, SECONDS));
		assertThat(result.getNotificationMessages()).isNotNull().hasSize(1);
		assertThat(result.getSignatoryReminder()).isNotNull();
		assertThat(result.getInitiator()).isNotNull();
		assertThat(result.getAdditionalParties()).isNotNull().hasSize(1);
		assertThat(result.getSignatories()).isNotNull().hasSize(1);
		assertThat(result.getDocument()).isNotNull();
		assertThat(result.getLanguage()).isEqualTo(language);
		assertThat(result.getDocumentAttachments()).isNotNull().hasSize(1);
	}

	@Test
	void toCreateSigningInstanceRequestType() {
		// Arrange
		final var signingRequest = SigningRequest.builder().build();

		// Act
		final var result = SigningMapper.toCreateSigningInstanceRequestType(signingRequest);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("credentials", "requestingSource", "custom", "requestId");
	}

	@Test
	void toSignatoryType() {
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
		final var identifications = List.of(Identification.builder().build());
		final var notificationMessage = NotificationMessage.builder()
			.withSubject(subject)
			.withBody(body)
			.withLanguage(language)
			.build();
		final var party = se.sundsvall.comfactfacade.api.model.Signatory.builder()
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
		final var result = SigningMapper.toSignatoryType(party);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("signatoryAction", "accountId", "custom", "personalNumber");
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getEmailAddress()).isEqualTo(emailAddress);
		assertThat(result.getMobilePhoneNumber()).isEqualTo(mobilePhoneNumber);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getLanguage()).isEqualTo(language);
		assertThat(result.getIdentifications()).isNotNull().hasSize(1);
		assertThat(result.getNotificationMessage()).isNotNull().hasNoNullFieldsOrProperties();
	}

	@Test
	void toMessageType() {
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
		final var result = SigningMapper.toMessageType(notificationMessage);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getSubject()).isEqualTo(subject);
		assertThat(result.getBody()).isEqualTo(body);
		assertThat(result.getLanguage()).isEqualTo(language);

	}

	@Test
	void toSignatoryReminderType() {
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
		final var result = SigningMapper.toSignatoryReminderType(reminder);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getReminderMessages()).isNotNull().hasSize(1);
		assertThat(result.getReminderMessages().getFirst().getSubject()).isEqualTo(subject);
		assertThat(result.getReminderMessages().getFirst().getBody()).isEqualTo(body);
		assertThat(result.getReminderMessages().getFirst().getLanguage()).isEqualTo(language);
		assertThat(result.isEnabled()).isEqualTo(enabled);
		assertThat(result.getHourInterval()).isEqualTo(intervalInHours);
		assertThat(result.getStartDate().toGregorianCalendar().toZonedDateTime()).isCloseTo(startDateTime.toZonedDateTime(), within(1, SECONDS));

	}

	@Test
	void toPartyType() {
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
		final var result = SigningMapper.toPartyType(party);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("accountId", "custom", "personalNumber");
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getOrganization()).isEqualTo(organization);
		assertThat(result.getEmailAddress()).isEqualTo(emailAddress);
		assertThat(result.getMobilePhoneNumber()).isEqualTo(mobilePhoneNumber);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getLanguage()).isEqualTo(language);

	}

	@Test
	void toDocumentTypeList() {
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
		final var result = SigningMapper.toDocumentTypeList(List.of(document));

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isNotNull().hasNoNullFieldsOrPropertiesExcept("encoding", "size", "documentPassword", "documentId");
		assertThat(result.getFirst().getDocumentName()).isEqualTo(documentName);
		assertThat(result.getFirst().getFileName()).isEqualTo(fileName);
		assertThat(result.getFirst().getMimeType()).isEqualTo(mimeType);
		assertThat(result.getFirst().getContent()).isEqualTo(Base64.decode(content));
	}

	@Test
	void toDocumentType() {
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
		final var result = SigningMapper.toDocumentType(document);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("encoding", "size", "documentPassword", "documentId");
		assertThat(result.getDocumentName()).isEqualTo(documentName);
		assertThat(result.getFileName()).isEqualTo(fileName);
		assertThat(result.getMimeType()).isEqualTo(mimeType);
		assertThat(result.getContent()).isEqualTo(Base64.decode(content));
	}

	@Test
	void toXMLGregorianCalendar() {
		// Arrange
		final var offsetDateTime = now();

		// Act
		final var result = SigningMapper.toXMLGregorianCalendar(offsetDateTime);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.toGregorianCalendar().toZonedDateTime()).isCloseTo(ZonedDateTime.now(), within(1, SECONDS));
	}

	@Test
	void toOffsetDateTime() throws DatatypeConfigurationException {
		// Arrange
		final var zonedDateTime = now().atZoneSameInstant(ZoneId.systemDefault());
		final var gregorianCalendar = GregorianCalendar.from(zonedDateTime);
		final var xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

		// Act
		final var result = SigningMapper.toOffsetDateTime(xmlGregorianCalendar);

		// Assert
		assertThat(result).isNotNull().isCloseTo(now(), within(1, SECONDS));
	}

	@Test
	void toDocument() {
		// Arrange
		final var documentName = "documentName";
		final var fileName = "fileName";
		final var mimeType = "mimeType";
		final var content = "content".getBytes();

		final var documentType = new DocumentType()
			.withDocumentName(documentName)
			.withFileName(fileName)
			.withMimeType(mimeType)
			.withContent(content);
		// Act
		final var result = SigningMapper.toDocument(documentType);

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
		final var statusCode = "statusCode";
		final var statusMessage = "statusMessage";
		final var status = new comfact.Status()
			.withStatusCode(statusCode)
			.withStatusMessage(statusMessage);

		// Act
		final var result = SigningMapper.toStatus(status);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getCode()).isEqualTo(statusCode);
		assertThat(result.getMessage()).isEqualTo(statusMessage);
	}

	@Test
	void toGetSigningInstanceInfoRequest() {
		// Arrange
		final var pageable = PageRequest.of(0, 10).withSort(Sort.by(Sort.Order.desc("Created")));

		// Act
		final var result = SigningMapper.toGetSigningInstanceInfoRequest(pageable);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("credentials", "requestingSource", "requestId");
		assertThat(result.getCustom()).isNotNull();
		assertThat(result.getCustom().getAnies()).hasSize(1);

	}

	@Test
	void toSigningsResponse() {
		// Arrang
		final var page = 0;
		final var pageSize = 10;
		final var totalItems = 20;
		final var orderByProperty = "Created";
		final var orderByDescending = true;

		final var totalPages = totalItems / pageSize;
		final var singingId = "123";

		final var paginator = new Paginator()
			.withPage(page)
			.withPageSize(pageSize)
			.withOrderByDescending(orderByDescending)
			.withTotalItems(totalItems)
			.withOrderByProperty(orderByProperty);

		final var doc = SigningMapper.toDocument(paginator);
		final var custom = new Custom().withAnies(doc);

		final var signingInstanceInfo = new SigningInstanceInfo()
			.withSigningInstanceId(singingId)
			.withStatus(new comfact.Status().withStatusCode("OK"));

		final var response = new comfact.GetSigningInstanceInfoResponse()
			.withSigningInstanceInfos(signingInstanceInfo)
			.withCustom(custom);

		// Act
		final var result = SigningMapper.toSigningsResponse(response);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getSigningInstances()).hasSize(1);
		assertThat(result.getSigningInstances().getFirst().getSigningId()).isEqualTo(singingId);

		assertThat(result.getPagingAndSortingMetaData().getPage()).isEqualTo(page);
		assertThat(result.getPagingAndSortingMetaData().getCount()).isEqualTo(1);
		assertThat(result.getPagingAndSortingMetaData().getLimit()).isEqualTo(pageSize);
		assertThat(result.getPagingAndSortingMetaData().getTotalRecords()).isEqualTo(totalItems);
		assertThat(result.getPagingAndSortingMetaData().getTotalPages()).isEqualTo(totalPages);
	}


	@Test
	void toSigningsResponse_noPaging() {
		// Arrange
		final var singingId = "123";
		final var signingInstanceInfo = new SigningInstanceInfo()
			.withSigningInstanceId(singingId)
			.withStatus(new comfact.Status().withStatusCode("OK"));

		final var response = new comfact.GetSigningInstanceInfoResponse()
			.withSigningInstanceInfos(signingInstanceInfo);

		// Act
		final var result = SigningMapper.toSigningsResponse(response);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("pagingAndSortingMetaData");
		assertThat(result.getSigningInstances()).hasSize(1);
		assertThat(result.getSigningInstances().getFirst().getSigningId()).isEqualTo(singingId);
		assertThat(result.getPagingAndSortingMetaData()).isNull();
	}

	@Test
	void toSigningsResponse_missingCustom() throws ParserConfigurationException {
		// Arrange
		final var document = DocumentBuilderFactory
			.newInstance()
			.newDocumentBuilder()
			.newDocument();

		// Arrange
		final var singingId = "123";
		final var signingInstanceInfo = new SigningInstanceInfo()
			.withSigningInstanceId(singingId)
			.withStatus(new comfact.Status().withStatusCode("OK"));

		final var response = new comfact.GetSigningInstanceInfoResponse()
			.withSigningInstanceInfos(signingInstanceInfo)
			.withCustom(new Custom().withAnies(document.getDocumentElement()));

		// Act
		final var result = SigningMapper.toSigningsResponse(response);

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("pagingAndSortingMetaData");
		assertThat(result.getSigningInstances()).hasSize(1);
		assertThat(result.getSigningInstances().getFirst().getSigningId()).isEqualTo(singingId);
		assertThat(result.getPagingAndSortingMetaData()).isNull();

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
