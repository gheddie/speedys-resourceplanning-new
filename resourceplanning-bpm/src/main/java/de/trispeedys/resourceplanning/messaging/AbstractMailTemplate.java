package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;

public abstract class AbstractMailTemplate<T>
{
    private Event event;
    
    public AbstractMailTemplate(Event event)
    {
        this.event = event;
    }

    public static String sincerely()
    {
        return AppConfiguration.getInstance().getText(AbstractMailTemplate.class, "speedysSincerely");
    }
    
    protected String helperGreeting(Helper helper)
    {
        return AppConfiguration.getInstance().getText(AbstractMailTemplate.class, "helperGreeting",
                helper.getFirstName());
    }  
    
    protected String getBaseLink()
    {
        return AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.HOST) +
                "/resourceplanning-bpm-" +
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.VERSION);
    }
    
    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }
}