package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.EventTemplate;

public class EventTemplateBuilder extends AbstractEntityBuilder<EventTemplate>
{
    private String description;
    
    public EventTemplateBuilder withDescription(String aDescription)
    {
        description = aDescription;
        return this;
    }

    public EventTemplate build()
    {
        EventTemplate eventTemplate = new EventTemplate();
        eventTemplate.setDescription(description);
        return eventTemplate;
    }
}