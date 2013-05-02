package org.motech.whpreports.performancetests.loaddata;

import org.databene.benerator.main.Benerator;
import org.junit.Test;

import java.io.IOException;

public class LoadReportingData {

    @Test
    public void shouldLoadPatientsAndProviders() throws IOException {
        try{
            System.setProperty("provider_count", String.valueOf(16000));
            //10 patients every provider
            System.setProperty("patient_count", String.valueOf(10));
            Benerator.main(new String[]{"createPatientProviderDataForReporting.ben.xml"});
        }catch (Exception exception){
            exception.printStackTrace();
        };
    }


    @Test
    public void shouldLoadAdherenceData() throws IOException {
        try{
            //10 times patients
            System.setProperty("adherence_audit_log_count", String.valueOf(1600000));
            Benerator.main(new String[]{"createAdherenceDataForReporting.ben.xml"});
        }catch (Exception exception){
            exception.printStackTrace();
        };
    }

    @Test
    public void shouldLoadContainerData() {
        try{
            System.setProperty("container_count", String.valueOf(100000));
            System.setProperty("container_call_log_count", String.valueOf(100000));
            Benerator.main(new String[]{"createContainerDataForReporting.ben.xml"});
        }catch (Exception exception){
            exception.printStackTrace();
        };
    }

    @Test
    public void shouldLoadReminderDataForProviderAndPatients() {
        try{
            System.setProperty("provider_reminder_call_log_count", String.valueOf(100000));
            Benerator.main(new String[]{"createRemindersDataForReporting.ben.xml"});
        }catch (Exception exception){
            exception.printStackTrace();
        };
    }

    @Test
    public void shouldLoadCallLogReports() {
        try{
            System.setProperty("call_log_count", String.valueOf(100000));
            Benerator.main(new String[]{"patientAlertCallLogs.ben.xml"});
        }catch (Exception exception){
            exception.printStackTrace();
        };
    }

}
