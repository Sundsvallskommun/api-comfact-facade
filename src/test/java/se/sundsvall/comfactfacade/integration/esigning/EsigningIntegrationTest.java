package se.sundsvall.comfactfacade.integration.esigning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.comfactfacade.integration.esigning.model.ComfactEventNotification;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

@ExtendWith(MockitoExtension.class)
class EsigningIntegrationTest {

	@Mock
	private EsigningClient esigningClientMock;

	@InjectMocks
	private EsigningIntegration integration;

	@Test
	void forwardComfactEvent() {
		final var event = new ComfactEventNotification("ref", "comfact-123", "cust", "completed", null, null, null, null);

		integration.forwardComfactEvent(MUNICIPALITY_ID, event);

		verify(esigningClientMock).forwardComfactEvent(MUNICIPALITY_ID, event);
		verifyNoMoreInteractions(esigningClientMock);
	}
}
