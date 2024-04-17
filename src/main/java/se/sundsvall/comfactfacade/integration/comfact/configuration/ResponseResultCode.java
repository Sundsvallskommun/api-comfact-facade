package se.sundsvall.comfactfacade.integration.comfact.configuration;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum ResponseResultCode {
	SUCCESS("Success", 200),
	GENERAL_ERROR("GeneralError", 502),
	DATABASE_ERROR("DatabaseError", 502),
	WORKFLOW_ERROR("WorkflowError", 502),
	CLIENT_AUTHENTICATION_FAILED("ClientAuthenticationFailed", 500),
	SIGNATORY_NOT_ACTIVATED("SignatoryNotActivated", 500),
	SIGNATORY_AUTHENTICATION_FAILED("SignatoryAuthenticationFailed", 500),
	SIGNATORY_AUTHENTICATION_LIMIT_EXCEEDED("SignatoryAuthenticationLimitExceeded", 500),
	DOWNLOAD_AUTHENTICATION_LIMIT_EXCEEDED("DownloadAuthenticationLimitExceeded", 500),
	CLIENT_AUTHORIZATION_FAILED("ClientAuthorizationFailed", 500),
	OPERATION_NOT_IMPLEMENTED("OperationNotImplemented", 502),
	REQUIRED_INPUT_NOT_PROVIDED("RequiredInputNotProvided", 400),
	REQUIRED_SIGNATURE_RESPONSE_NOT_PROVIDED("RequiredSignatureResponseNotProvided", 400),
	INVALID_INPUT_PROVIDED("InvalidInputProvided", 400),
	INVALID_FOR_UPDATE("InvalidForUpdate", 400),
	INVALID_FOR_UPDATE_SIGNING_INSTANCE_STATUS("InvalidForUpdateSigningInstanceStatus", 400),
	INVALID_FOR_UPDATE_EXPIRY_DATE("InvalidForUpdateExpiryDate", 400),
	INVALID_FOR_UPDATE_SIGNATORY_REMINDER("InvalidForUpdateSignatoryReminder", 400),
	INVALID_FOR_UPDATE_SIGNATORY_DISPLAY("InvalidForUpdateSignatoryDisplay", 400),
	INVALID_FOR_UPDATE_SIGNATORY_IDENTITY("InvalidForUpdateSignatoryIdentity", 400),
	DOCUMENT_TYPE_NOT_SUPPORTED("DocumentTypeNotSupported", 400),
	OPTIONAL_INPUT_NOT_SUPPORTED("OptionalInputNotSupported", 400),
	SIGNING_INSTANCE_SYNCHRONIZATION_ERROR("SigningInstanceSynchronizationError", 500),
	SIGNING_INSTANCE_NOT_FOUND("SigningInstanceNotFound", 404),
	SERVICE_REQUEST_XML_VALIDATION_ERROR("ServiceRequestXMLValidationError", 400),
	SIGNING_INSTANCE_XML_VALIDATION_ERROR("SigningInstanceXMLValidationError", 400),
	SIGNING_INSTANCE_NOT_RUNNABLE("SigningInstanceNotRunnable", 500),
	SIGNING_INSTANCE_COMPLETED("SigningInstanceCompleted", 200),
	SIGNING_INSTANCE_WITHDRAWN("SigningInstanceWithdrawn", 200),
	SIGNING_INSTANCE_APPROVED("SigningInstanceApproved", 200),
	SIGNING_INSTANCE_DECLINED("SigningInstanceDeclined", 200),
	SIGNING_INSTANCE_EXPIRED("SigningInstanceExpired", 200),
	SIGNATORY_NOT_FOUND("SignatoryNotFound", 404),
	SIGNATORY_APPROVED("SignatoryApproved", 200),
	SIGNATORY_SYNCHRONIZATION_ERROR("SignatorySynchronizationError", 500),
	INVALID_IDENTIFICATION_ALIAS("InvalidIdentificationAlias", 400),
	INVALID_SERVICE_PROVIDER_CONFIGURATION("InvalidServiceProviderConfiguration", 500),
	IDENTITY_PROVIDER_CONTRACT_NOT_FOUND("IdentityProviderContractNotFound", 404),
	INVALID_IDENTITY_PROVIDER_CONTRACT("InvalidIdentityProviderContract", 400),
	IDENTITY_PROVIDER_CONFIGURATION_NOT_FOUND("IdentityProviderConfigurationNotFound", 404),
	INVALID_IDENTITY_PROVIDER_CONFIGURATION("InvalidIdentityProviderConfiguration", 400),
	INVALID_IDENTIFICATION_TOKEN("InvalidIdentificationToken", 400),
	INVALID_SIGNATURE_RESPONSE("InvalidSignatureResponse", 400),
	IDENTIFICATION_TOKEN_NOT_AUTHENTICATED("IdentificationTokenNotAuthenticated", 500),
	IDENTIFICATION_TYPE_DOES_NOT_SUPPORT_ACTION("IdentificationTypeDoesNotSupportAction", 500),
	SIGNATURE_REQUEST_CREATION_FAILED("SignatureRequestCreationFailed", 500),
	SIGNATURE_RESPONSE_SERVICE_ERROR("SignatureResponseServiceError", 500),
	SIGNATURE_RESPONSE_USER_MISMATCH("SignatureResponseUserMismatch", 500),
	SIGNATURE_RESPONSE_SIGN_MESSAGE_ERROR("SignatureResponseSignMessageError", 400),
	SIGNATURE_TIMESTAMP_FAILED("SignatureTimestampFailed", 500),
	SIGNATURE_CERTIFICATE_VERIFICATION_FAILED("SignatureCertificateVerificationFailed", 500),
	SIGNATURE_TIMESTAMP_VERIFICATION_FAILED("SignatureTimestampVerificationFailed", 500),
	AUGMENT_SIGNED_DOCUMENT_FAILED("AugmentSignedDocumentFailed", 500),
	TRAFFIC_MESSAGE_FAILED("TrafficMessageFailed", 500),
	CLIENT_SERVICE_UPDATE_FAILED("ClientServiceUpdateFailed", 500),
	EMAIL_UPDATE_FAILED("EmailUpdateFailed", 500),
	DOWN_FOR_MAINTENANCE("DownForMaintenance", 502),
	UNKNOWN_COMFACT_ERROR("UnknownComfactError", 500);

	private final String message;

	private final int httpCode;

	ResponseResultCode(final String message, final int httpCode) {
		this.message = message;
		this.httpCode = httpCode;
	}

	public static ResponseResultCode fromMessage(final String message) {
		return Arrays.stream(values())
			.filter(code -> code.message.equals(message)).findFirst()
			.orElse(UNKNOWN_COMFACT_ERROR);
	}

}
