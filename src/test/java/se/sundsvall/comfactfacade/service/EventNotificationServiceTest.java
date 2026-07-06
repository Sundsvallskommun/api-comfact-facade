package se.sundsvall.comfactfacade.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.comfactfacade.integration.esigning.EsigningIntegration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;
import static se.sundsvall.comfactfacade.TestUtil.createEventNotification;

@ExtendWith(MockitoExtension.class)
class EventNotificationServiceTest {

	@Mock
	private EsigningIntegration esigningIntegrationMock;

	@InjectMocks
	private EventNotificationService service;

	@Test
	void handleComfactEvent() {
		final var notification = createEventNotification();

		service.handleComfactEvent(MUNICIPALITY_ID, notification);

		verify(esigningIntegrationMock).forwardComfactEvent(MUNICIPALITY_ID, EventNotificationMapper.toEsigningEvent(notification));
		verifyNoMoreInteractions(esigningIntegrationMock);
	}
}
