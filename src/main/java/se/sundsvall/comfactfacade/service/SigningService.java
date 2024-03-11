package se.sundsvall.comfactfacade.service;

import org.springframework.stereotype.Service;

import se.sundsvall.comfactfacade.api.model.Document;
import se.sundsvall.comfactfacade.api.model.SigningRequest;


@Service
public class SigningService {

	public String createSigningRequest(final SigningRequest signingRequest) {
		return "OK";
	}

	public String cancelSigningRequest(final String signingId) {
		return "OK";
	}

	public String getStatus(final String signingId) {
		return "OK";
		// Do something cool with this signing id
	}

	public Document getSignedDocument(final String signingId) {
		return new Document(null, "", "", "");
	}


}
