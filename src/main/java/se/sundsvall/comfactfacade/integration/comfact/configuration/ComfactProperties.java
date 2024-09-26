package se.sundsvall.comfactfacade.integration.comfact.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.comfact")
public record ComfactProperties(
	String username,
	String password,
	String url,
	int connectTimeout,
	int readTimeout) {
}
