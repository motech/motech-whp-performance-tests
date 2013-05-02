package org.motechproject.whp.performancetests.loaddata;

import org.databene.benerator.main.Benerator;
import org.junit.Test;

import java.io.IOException;

public class LoadPatientsAndAdherenceLogs {
    @Test
    public void shouldLoadPatientsAndProviders() throws IOException {
        System.setProperty("provider_count", String.valueOf(10000));
        Benerator.main(new String[]{"createProvidersAndPatients.ben.xml"});
    }

    @Test
    public void shouldLoadAdherenceLogs() throws IOException {
        System.setProperty("count", String.valueOf(160000));
        Benerator.main(new String[]{"adherenceLog.ben.xml"});
    }
}
