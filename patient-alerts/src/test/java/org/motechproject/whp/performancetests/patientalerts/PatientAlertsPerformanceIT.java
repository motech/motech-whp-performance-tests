package org.motechproject.whp.performancetests.patientalerts;

import org.databene.benerator.main.Benerator;
import org.junit.Test;

import java.io.IOException;

public class PatientAlertsPerformanceIT {

    @Test
    public void should() throws IOException {
        System.setProperty("provider_count", String.valueOf(1));
        System.setProperty("patient_count", String.valueOf(1));
        System.setProperty("count", String.valueOf(1));
        Benerator.main(new String[]{"createProvidersAndPatients.ben.xml"});
        Benerator.main(new String[]{"adherenceLog.ben.xml"});
        Benerator.main(new String[]{"quartz.ben.xml"});
    }

}
