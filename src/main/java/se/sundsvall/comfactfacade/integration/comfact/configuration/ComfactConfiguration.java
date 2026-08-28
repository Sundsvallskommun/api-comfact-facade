package se.sundsvall.comfactfacade.integration.comfact.configuration;

import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;
import se.sundsvall.dept44.configuration.feign.retryer.ActionRetryer;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Configuration
@EnableConfigurationProperties(ComfactProperties.class)
@Import(FeignConfiguration.class)
public class ComfactConfiguration {

	public static final String CLIENT_ID = "comfact";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final ComfactProperties properties) {
		// All Comfact credentials are data-driven: the account-aware interceptor selects them per request
		// (ComfactAccountContext). The ActionRetryer mirrors the dept44 wiring - one retry with token eviction on
		// rejected tokens.
		final var oAuth2Interceptor = new AccountAwareOAuth2RequestInterceptor(properties.tokenUrl());
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID, List.of(BAD_REQUEST.value(), NOT_FOUND.value(), CONFLICT.value())))
			.withRequestInterceptor(oAuth2Interceptor)
			.withCustomizer(builder -> builder.retryer(new ActionRetryer(oAuth2Interceptor::removeToken, 1)))
			.withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
			.composeCustomizersToOne();
	}
}
