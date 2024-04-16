package se.sundsvall.comfactfacade.integration.comfact.configuration;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.soap.MessageFactory;

import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import comfact.GetSigningInstanceInfoResponse;
import feign.Response;
import feign.codec.ErrorDecoder;

public class ComfactErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder defaultDecoder = new Default();

	@Override
	public Exception decode(final String methodKey, final Response response) {
		try {
			final var responseStream = response.body().asInputStream();
			final var soapMessage = MessageFactory.newInstance().createMessage(null, responseStream);

			final var jaxbContext = JAXBContext.newInstance(GetSigningInstanceInfoResponse.class);
			final var jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			final var getSigningInstanceInfoResponse = (GetSigningInstanceInfoResponse) jaxbUnmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument());

			if (!getSigningInstanceInfoResponse.getResult().isSuccess()) {

				final var resultCode = getSigningInstanceInfoResponse.getResult().getResultCode();
				final var resultMessage = getSigningInstanceInfoResponse.getResult().getResultMessage();

				final var responseResultCode = ResponseResultCode.fromMessage(resultCode);
				return Problem.valueOf(Status.valueOf(responseResultCode.getHttpCode()), resultMessage);
			}
		} catch (final Exception e) {
			return Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Failed to decode SOAP response");
		}
		return defaultDecoder.decode(methodKey, response);
	}

}
