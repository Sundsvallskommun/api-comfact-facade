package se.sundsvall.comfactfacade.integration.comfact.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.comfactfacade.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class ComfactPropertiesTest {

	@Autowired
	private ComfactProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(5);
		assertThat(properties.readTimeout()).isEqualTo(30);
	}

}
