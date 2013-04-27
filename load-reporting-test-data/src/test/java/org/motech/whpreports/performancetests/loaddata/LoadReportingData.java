package org.motech.whpreports.performancetests.loaddata;

import org.databene.benerator.main.Benerator;
import org.junit.Test;

import java.io.IOException;

public class LoadReportingData {

    @Test
    public void shouldLoadPatientsAndProviders() throws IOException {
        try{
            System.setProperty("count", String.valueOf(100));
            Benerator.main(new String[]{"createDataForReporting.ben.xml"});
        }catch (Exception exception){

        };
    }
}
