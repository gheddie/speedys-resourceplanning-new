package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;

public abstract class AbstractMailTemplate<T>
{
    private GuidedEvent event;
    
    public AbstractMailTemplate(GuidedEvent event)
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
    
    public GuidedEvent getEvent()
    {
        return event;
    }

    public void setEvent(GuidedEvent event)
    {
        this.event = event;
    }
    
    public abstract String constructBody();

    public abstract String constructSubject();

    public abstract MessagingType getMessagingType();
}