package de.trispeedys.resourceplanning.singleton;

import de.trispeedys.resourceplanning.webservice.ResourceInfo;
import de.trispeedys.resourceplanning.webservice.ResourceInfoService;

public class AppSingleton
{
    private static AppSingleton instance;
    
    private ResourceInfo resourceInfo = null;

    private AppSingleton()
    {
        resourceInfo = new ResourceInfoService().getResourceInfoPort();
    }

    public static AppSingleton getInstance()
    {
        if (AppSingleton.instance == null)
        {
            AppSingleton.instance = new AppSingleton();
        }
        return AppSingleton.instance;
    }
    
    public ResourceInfo getPort()
    {
        return this.resourceInfo;
    }
}