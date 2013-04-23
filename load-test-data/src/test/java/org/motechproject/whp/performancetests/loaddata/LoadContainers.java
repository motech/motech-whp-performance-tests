package org.motechproject.whp.performancetests.loaddata;

import org.databene.benerator.main.Benerator;
import org.junit.Test;

import java.io.IOException;

public class LoadContainers {
    @Test
    public void shouldLoadPatientsAndProviders() throws IOException {
        System.setProperty("count", String.valueOf(300000));
        Benerator.main(new String[]{"containers.ben.xml"});
    }
}
