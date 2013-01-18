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
import org.motechproject.whp.providerreminder.ivr.ProviderReminderRequestProperties;
import org.motechproject.whp.providerreminder.service.ProviderReminderService;
import org.motechproject.whp.providerreminder.service.ReminderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.motechproject.whp.common.event.EventKeys.ADHERENCE_NOT_REPORTED_EVENT_NAME;
import static org.motechproject.whp.common.event.EventKeys.ADHERENCE_WINDOW_COMMENCED_EVENT_NAME;

@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations ={"classpath*:/applicationProviderReminderContext.xml"})
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
    ProviderReminderRequestProperties providerReminderRequestProperties;

    @Before
    public void setUp() throws Exception {
        testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);
    }

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { 10, 10 }, { 100, 110 }, { 500, 610 }, { 1000, 1610 } };
        return asList(data);
    }

    @Test
    public void shouldSendReminderForProviders() throws IOException {
        System.setProperty("provider_count", String.valueOf(numberOfProvidersToAdd));
        Benerator.main(new String[]{"createProvidersAndPatients.ben.xml"});

        System.setProperty("count", String.valueOf(numberOfProvidersToAdd * 10));
        Benerator.main(new String[]{"adherenceLog.ben.xml"});

        System.out.println("-- before indexing couchdb views ---");
        executeReminderEvents();
        System.out.println("-- after priming couchdb views ---");
        executeReminderEvents();
    }

    private void executeReminderEvents() {
        executeAndLogWindowApproachingReminder();
        executeAndLogAdherenceNotReportedReminder();
    }

    private void executeAndLogWindowApproachingReminder() {
        StopWatch stopWatch = new StopWatch(String.format("adherence-approaching-reminder-%s-providers", numberOfProviders));
        stopWatch.start();

        reminderEventHandler.adherenceWindowCommencedEvent(new MotechEvent(ADHERENCE_WINDOW_COMMENCED_EVENT_NAME));

        stopWatch.stop();

        logRequestDetails();
        logStopWatchSummary(stopWatch);
    }

    private void executeAndLogAdherenceNotReportedReminder() {
        StopWatch stopWatch = new StopWatch(String.format("adherence-not-reported-reminder-%s-providers", numberOfProviders));
        stopWatch.start();

        reminderEventHandler.adherenceNotReportedEvent(new MotechEvent(ADHERENCE_NOT_REPORTED_EVENT_NAME));

        stopWatch.stop();

        logRequestDetails();
        logStopWatchSummary(stopWatch);
    }

    private void logStopWatchSummary(StopWatch stopWatch) {
        logger.info(stopWatch.shortSummary());
    }


    private void logRequestDetails() {
        ArgumentCaptor<String> reminderXmlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClientService, atLeastOnce()).post(eq(providerReminderRequestProperties.getProviderReminderUrl()), reminderXmlCaptor.capture(), any(HashMap.class));
        for(String reminderXml : reminderXmlCaptor.getAllValues()){
            logger.debug("Reminder Request: " + reminderXml);
        }
        reset(httpClientService);
    }
}
