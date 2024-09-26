package se.sundsvall.comfactfacade.integration.comfact.configuration;

import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import comfact.ResponseType;
import comfact.Result;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.soap.MessageFactory;

public class ComfactErrorDecoder implements ErrorDecoder {

	private static final JAXBContext jaxbContext;

	static {
		try {
			jaxbContext = JAXBContext.newInstance(ResponseType.class);
		} catch (final Exception e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Failed to create JAXBContext");
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
			return Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Failed to decode SOAP response");
		}
	}

	private ThrowableProblem toThrowableProblem(final Result result) {
		final var resultCode = result.getResultCode();
		final var resultMessage = result.getResultMessage();

		final var responseResultCode = ResponseResultCode.fromMessage(resultCode);
		return Problem.valueOf(Status.valueOf(responseResultCode.getHttpCode()), resultMessage);
	}
}
