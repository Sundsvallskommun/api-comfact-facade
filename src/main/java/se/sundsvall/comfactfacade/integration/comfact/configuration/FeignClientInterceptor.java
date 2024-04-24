package se.sundsvall.comfactfacade.integration.comfact.configuration;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.hc.core5.http.ContentType.TEXT_XML;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.apache.hc.core5.http.ContentType;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import feign.InvocationContext;
import feign.Response;
import feign.ResponseInterceptor;

@Component
public class FeignClientInterceptor implements ResponseInterceptor {
	private static final String NOT_SUCCESSFUL = "<Result success=\"false\">";

	@Override
	public Object intercept(InvocationContext invocationContext, Chain chain) throws Exception {
		final var response = invocationContext.response();
		final var body = new String(response.body().asInputStream().readAllBytes());
		final var contentType = ofNullable(response.headers().get(CONTENT_TYPE)).orElse(emptyList())
			.stream()
			.findFirst()
			.orElse(null);

		if (TEXT_XML.isSameMimeType(ContentType.parse(contentType)) && body.contains(NOT_SUCCESSFUL)) {
			ofNullable(ReflectionUtils.findField(Response.class, "status")).ifPresent(field -> {
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, response, 500);
			});
		}

		return invocationContext.proceed();
	}
}
