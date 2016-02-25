package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;

public class AppConfigurationTest
{
    @Test
    public void testAppConfigurationTest()
    {
        System.out.println(AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL));
    }
}