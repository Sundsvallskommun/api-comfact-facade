package se.sundsvall.comfactfacade.integration.comfact.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import feign.Response;


@ExtendWith(MockitoExtension.class)
class ComfactErrorDecoderTest {

	@InjectMocks
	ComfactErrorDecoder comfactErrorDecoder;

	@Mock
	Response response;

	@Mock
	Response.Body body;

	@Test
	void decode() throws IOException {
		when(response.body()).thenReturn(body);
		when(body.asInputStream()).thenReturn(new ByteArrayInputStream("""
				<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
				   <soap:Body>
				      <GetSigningInstanceInfoResponse xmlns="urn:comfact:prosale:signing:2.0:protocol">
				         <Result success="false">
				            <ResultCode>DatabaseError</ResultCode>
				            <ResultMessage>A database error occurred that can not be automatically repaired. [EventId:fd93ea28ac094e2e83b0a1b6c02058d6]</ResultMessage>
				         </Result>
				      </GetSigningInstanceInfoResponse>
				   </soap:Body>
				</soap:Envelope>
			""".getBytes()));

		final var result = comfactErrorDecoder.decode("methodKey", response);

		verify(response, times(1)).body();
		verify(body, times(1)).asInputStream();

		assertThat(result).isNotNull();
		assertThat(result.getMessage()).isEqualTo("Bad Gateway: A database error occurred that can not be automatically repaired. [EventId:fd93ea28ac094e2e83b0a1b6c02058d6]");
	}

}
