package com.kasirpintar.automation.context;

import com.kasirpintar.automation.pages.BarangDetailPage;
import com.kasirpintar.automation.pages.BarangFormPage;
import com.kasirpintar.automation.pages.BarangListPage;
import com.kasirpintar.automation.pages.ManagementPage;
import com.kasirpintar.automation.pages.SalesHomePage;

/**
 * Per-scenario shared state, injected (PicoContainer) into every step-definition
 * class so they can hand pages to each other. Cucumber creates ONE instance of
 * this per scenario and passes the same one to LoginSteps, BarangSteps, etc.
 */
public class ScenarioContext {

    public SalesHomePage salesHome;
    public ManagementPage managementPage;
    public BarangListPage listPage;
    public BarangFormPage formPage;
    public BarangDetailPage detailPage;
}
