package org.motechproject.whp.performancetests.loaddata;

import org.databene.benerator.main.Benerator;
import org.junit.Test;

import java.io.IOException;

public class LoadAdherenceRecords {

    @Test
    public void shouldLoadPatientsAndProviders() throws IOException {
        System.setProperty("count", String.valueOf(100000));
        Benerator.main(new String[]{"adherenceLog.ben.xml"});
    }

}
