package org.motechproject.whp.performancetests.providerreminder;

import org.apache.log4j.Logger;
import org.databene.benerator.main.Benerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.ArgumentCaptor;
import org.motechproject.event.MotechEvent;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.whp.common.service.IvrConfiguration;
import org.motechproject.whp.providerreminder.service.ProviderReminderService;
import org.motechproject.whp.providerreminder.service.ReminderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.motechproject.whp.common.event.EventKeys.ADHERENCE_WINDOW_APPROACHING_EVENT_NAME;

@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations ={"classpath*:/applicationProviderReminderContext.xml", "classpath:applicationContextPerformanceIT.xml"})
public class ReminderEventHandlerPerformanceIT {

    private int numberOfProvidersToAdd;
    private int numberOfProviders;

    private TestContextManager testContextManager;


    public ReminderEventHandlerPerformanceIT(int numberOfProvidersToAdd, int numberOfProviders) {
        this.numberOfProvidersToAdd = numberOfProvidersToAdd;
        this.numberOfProviders = numberOfProviders;
    }

    @ReplaceWithMock
    @Autowired
    HttpClientService httpClientService;

    @Autowired
    ReminderEventHandler reminderEventHandler;

    @Autowired
    ProviderReminderService providerReminderService;

    @Autowired
    IvrConfiguration ivrConfiguration;

    @Before
    public void setUp() throws Exception {
        testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);
    }

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { 10, 10 }, { 100, 100 }, { 500, 500 }, { 500, 1000 } };
        return asList(data);
    }

    @Test
    public void shouldSendReminderForProviders() throws IOException {
        Benerator.main(new String[]{String.format("createProvidersAndPatients.%s.ben.xml", numberOfProvidersToAdd)});

//        providerReminderService.getActiveProviderPhoneNumbers(); //prime the couchdb indexes

        StopWatch stopWatch = new StopWatch(String.format("reminder-%s-providers", numberOfProviders));
        stopWatch.start();

        reminderEventHandler.adherenceWindowApproachingEvent(new MotechEvent(ADHERENCE_WINDOW_APPROACHING_EVENT_NAME));

        stopWatch.stop();

        ArgumentCaptor<String> reminderXmlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClientService).post(eq(ivrConfiguration.getProviderReminderUrl()), reminderXmlCaptor.capture());
        String reminderXml = reminderXmlCaptor.getValue();

        logger.debug("Reminder Request: " + reminderXml);
        logger.info(stopWatch.shortSummary());

        reset(httpClientService);
    }
}
