package org.motechproject.calllog.report;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.databene.benerator.main.Benerator;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.io.IOException;

public class CallLogReportPerformanceIT {

    private int callLogCount = 70000;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    //    @Before
    public void setUp() throws IOException {

        System.setProperty("count", String.valueOf(callLogCount));
        Benerator.main(new String[]{"callLogs.ben.xml"});
    }

    @Test
    public void shouldLoadCallLogReports() throws IOException {
        StopWatch stopWatch = new StopWatch(String.format("call-log-download", callLogCount));
        stopWatch.start();

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:9999/whp-reports/download/patientAlertCallLog.xls");
        client.execute(request);

        stopWatch.stop();

        logger.info(stopWatch.shortSummary());

    }
}
