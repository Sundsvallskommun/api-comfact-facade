package se.sundsvall.comfactfacade.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomPageableConfiguration implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		final var resolver = new PageableHandlerMethodArgumentResolver();
		// Define fallback Pageable with default sort
		final var defaultPageable = PageRequest.of(0, 10, Sort.by("SigningInstanceId").ascending());
		resolver.setFallbackPageable(defaultPageable);
		resolvers.add(resolver);
	}
}
