package org.motechproject.whp.performancetests.patientalerts;

import org.databene.benerator.main.Benerator;
import org.junit.Test;

import java.io.IOException;

public class PatientAlertsPerformanceIT {

    @Test
    public void shouldCreateSchedules() throws IOException {
        int count = 100;
        System.setProperty("provider_count", String.valueOf(count));
        System.setProperty("patient_count", String.valueOf(1));
        System.setProperty("count", String.valueOf(count));
        Benerator.main(new String[]{"createProvidersAndPatients.ben.xml"});
        Benerator.main(new String[]{"adherenceLog.ben.xml"});
        Benerator.main(new String[]{"quartz.ben.xml"});
    }

}
