package se.sundsvall.comfactfacade.integration.comfact.configuration;

import static java.util.Collections.emptyMap;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.apache.hc.core5.http.ContentType.TEXT_XML;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import feign.InvocationContext;
import feign.Response;
import feign.Response.Body;
import feign.ResponseInterceptor.Chain;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeignClientInterceptorTest {

	@Mock
	private Chain chainMock;

	@Mock
	private InvocationContext invocationContextMock;

	@Mock
	private Response responseMock;

	@Mock
	private Body bodyMock;

	@Mock
	private InputStream inputStreamMock;

	private FeignClientInterceptor feignClientInterceptor = new FeignClientInterceptor();

	@Test
	void verifyImplements() {
		assertThat(feignClientInterceptor.getClass()).isAssignableFrom(FeignClientInterceptor.class);
	}

	@Test
	void interceptForSuccessfulXml() throws Exception {
		when(invocationContextMock.response()).thenReturn(responseMock);
		when(responseMock.body()).thenReturn(bodyMock);
		when(bodyMock.asInputStream()).thenReturn(inputStreamMock);
		when(responseMock.headers()).thenReturn(Map.of(CONTENT_TYPE, List.of(TEXT_XML.getMimeType())));
		when(responseMock.status()).thenCallRealMethod();
		when(inputStreamMock.readAllBytes()).thenReturn("""
			<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
				<soap:Body>
					<GetSigningInstanceInfoResponse responseId="?" xmlns="urn:comfact:prosale:signing:2.0:protocol">
						<Result success="true">
							<ResultCode>Success</ResultCode>
							<ResultMessage>The requested service operation was successfully completed.</ResultMessage>
						</Result>
					</GetSigningInstanceInfoResponse>
				</soap:Body>
			</soap:Envelope>
			""".getBytes());

		feignClientInterceptor.intercept(invocationContextMock, chainMock);

		assertThat(responseMock.status()).isZero();
		verify(invocationContextMock).response();
		verify(responseMock).body();
		verify(bodyMock).asInputStream();
		verify(inputStreamMock).readAllBytes();
		verify(responseMock).headers();
		verify(invocationContextMock).proceed();

		verifyNoMoreInteractions(chainMock, invocationContextMock, responseMock, bodyMock, inputStreamMock);
	}

	@Test
	void interceptForUnsuccessfulXml() throws Exception {
		when(invocationContextMock.response()).thenReturn(responseMock);
		when(responseMock.body()).thenReturn(bodyMock);
		when(bodyMock.asInputStream()).thenReturn(inputStreamMock);
		when(responseMock.headers()).thenReturn(Map.of(CONTENT_TYPE, List.of(TEXT_XML.getMimeType())));
		when(responseMock.status()).thenCallRealMethod();
		when(inputStreamMock.readAllBytes()).thenReturn("""
			<?xml version="1.0" encoding="utf-8" standalone="no"?>
			<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
				<soap:Body>
					<CreateSigningInstanceResponse xmlns="urn:comfact:prosale:signing:2.0:protocol">
						<Result success="false">
							<ResultCode>InvalidInputProvided</ResultCode>
							<ResultMessage>The input supplied by the client is invalid or could not be parsed. [EventId:7ecc626e03e64adb800ec5a550e843c7] Duplicate partyId for 70911785-28f2-4550-bca3-27abcf581fed</ResultMessage>
						</Result>
					</CreateSigningInstanceResponse>
				</soap:Body>
			</soap:Envelope>
			""".getBytes());

		feignClientInterceptor.intercept(invocationContextMock, chainMock);

		assertThat(responseMock.status()).isEqualTo(500);
		verify(invocationContextMock).response();
		verify(responseMock).body();
		verify(bodyMock).asInputStream();
		verify(inputStreamMock).readAllBytes();
		verify(responseMock).headers();
		verify(invocationContextMock).proceed();

		verifyNoMoreInteractions(chainMock, invocationContextMock, responseMock, bodyMock, inputStreamMock);
	}

	@Test
	void interceptForNonXml() throws Exception {
		when(invocationContextMock.response()).thenReturn(responseMock);
		when(responseMock.body()).thenReturn(bodyMock);
		when(bodyMock.asInputStream()).thenReturn(inputStreamMock);
		when(responseMock.headers()).thenReturn(Map.of(CONTENT_TYPE, List.of(APPLICATION_JSON.getMimeType())));
		when(responseMock.status()).thenCallRealMethod();
		when(inputStreamMock.readAllBytes()).thenReturn("{}".getBytes());

		feignClientInterceptor.intercept(invocationContextMock, chainMock);

		assertThat(responseMock.status()).isZero();
		verify(invocationContextMock).response();
		verify(responseMock).body();
		verify(bodyMock).asInputStream();
		verify(inputStreamMock).readAllBytes();
		verify(responseMock).headers();
		verify(invocationContextMock).proceed();

		verifyNoMoreInteractions(chainMock, invocationContextMock, responseMock, bodyMock, inputStreamMock);
	}

	@Test
	void interceptWhenContentTypeNotPresent() throws Exception {
		when(invocationContextMock.response()).thenReturn(responseMock);
		when(responseMock.body()).thenReturn(bodyMock);
		when(bodyMock.asInputStream()).thenReturn(inputStreamMock);
		when(responseMock.headers()).thenReturn(emptyMap());
		when(responseMock.status()).thenCallRealMethod();
		when(inputStreamMock.readAllBytes()).thenReturn("{}".getBytes());

		feignClientInterceptor.intercept(invocationContextMock, chainMock);

		assertThat(responseMock.status()).isZero();
		verify(invocationContextMock).response();
		verify(responseMock).body();
		verify(bodyMock).asInputStream();
		verify(inputStreamMock).readAllBytes();
		verify(responseMock).headers();
		verify(invocationContextMock).proceed();

		verifyNoMoreInteractions(chainMock, invocationContextMock, responseMock, bodyMock, inputStreamMock);
	}
}
