package se.sundsvall.comfactfacade.utility;

public class EncryptionException extends RuntimeException {

	private static final long serialVersionUID = -8492724459714632879L;

	public EncryptionException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
