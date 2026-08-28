package se.sundsvall.comfactfacade.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import se.sundsvall.comfactfacade.service.AccountService;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	private final AccountService accountService;

	public WebMvcConfiguration(final AccountService accountService) {
		this.accountService = accountService;
	}

	@Override
	public void addInterceptors(@NonNull final InterceptorRegistry registry) {
		// Signing endpoints only - the account CRUD endpoints must not resolve an account (bootstrapping).
		registry.addInterceptor(new ComfactAccountInterceptor(accountService))
			.addPathPatterns("/*/signings", "/*/signings/**");
	}
}
