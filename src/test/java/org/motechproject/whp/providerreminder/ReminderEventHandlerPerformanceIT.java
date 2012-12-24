package org.motechproject.whp.providerreminder;

import org.apache.log4j.Logger;
import org.databene.benerator.main.Benerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.ArgumentCaptor;
import org.motechproject.event.MotechEvent;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.whp.common.service.IvrConfiguration;
import org.motechproject.whp.providerreminder.service.ReminderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.motechproject.whp.common.event.EventKeys.ADHERENCE_WINDOW_APPROACHING_EVENT_NAME;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations ={"classpath*:/applicationProviderReminderContext.xml", "classpath:applicationContextPerformanceIT.xml"})
public class ReminderEventHandlerPerformanceIT {

    @ReplaceWithMock
    @Autowired
    HttpClientService httpClientService;

    @Autowired
    ReminderEventHandler reminderEventHandler;
    @Autowired
    IvrConfiguration ivrConfiguration;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Test
    public void shouldSendReminderFor1000Providers() throws IOException {
        Benerator.main(new String[]{"createProvidersAndPatients.ben.xml"});

        StopWatch stopWatch = new StopWatch("reminder-1000-providers");
        stopWatch.start();

        reminderEventHandler.adherenceWindowApproachingEvent(new MotechEvent(ADHERENCE_WINDOW_APPROACHING_EVENT_NAME));

        stopWatch.stop();

        System.out.println(stopWatch.shortSummary());

        ArgumentCaptor<String> reminderXmlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClientService).post(eq(ivrConfiguration.getProviderReminderUrl()), reminderXmlCaptor.capture());
        String reminderXml = reminderXmlCaptor.getValue();

        logger.debug("Reminder Request: " + reminderXml);
        logger.info(stopWatch.shortSummary());
    }
}
