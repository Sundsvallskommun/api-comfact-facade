package se.sundsvall.comfactfacade.configuration;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("municipality")
public record MunicipalityProperties(Map<String, Municipality> ids) {

	public record Municipality(
		String username,
		String password) {
	}
}
