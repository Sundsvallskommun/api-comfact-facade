package se.sundsvall.comfactfacade.integration.comfact.configuration;

import comfact.ResponseType;
import comfact.Result;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.soap.MessageFactory;
import org.springframework.http.HttpStatus;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;

public class ComfactErrorDecoder implements ErrorDecoder {

	private static final JAXBContext jaxbContext;

	static {
		try {
			jaxbContext = JAXBContext.newInstance(ResponseType.class);
		} catch (final Exception e) {
			throw Problem.valueOf(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create JAXBContext");
		}
	}

	@Override
	public Exception decode(final String methodKey, final Response response) {
		try {
			final var responseStream = response.body().asInputStream();
			final var soapMessage = MessageFactory.newInstance().createMessage(null, responseStream);
			final var jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			final var typedResponse = (ResponseType) jaxbUnmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument());
			return toThrowableProblem(typedResponse.getResult());
		} catch (final Exception e) {
			return Problem.valueOf(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to decode SOAP response");
		}
	}

	private ThrowableProblem toThrowableProblem(final Result result) {
		final var resultCode = result.getResultCode();
		final var resultMessage = result.getResultMessage();

		final var responseResultCode = ResponseResultCode.fromMessage(resultCode);
		return Problem.valueOf(HttpStatus.valueOf(responseResultCode.getHttpCode()), resultMessage);
	}
}
