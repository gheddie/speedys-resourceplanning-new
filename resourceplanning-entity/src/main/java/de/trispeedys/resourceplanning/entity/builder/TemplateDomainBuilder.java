package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.TemplateDomain;

public class TemplateDomainBuilder extends AbstractEntityBuilder<TemplateDomain>
{
    private EventTemplate template;
    
    private Domain domain;

    public TemplateDomainBuilder withEventTemplate(EventTemplate aTemplate)
    {
        template = aTemplate;
        return this;
    }
    
    public TemplateDomainBuilder withDomain(Domain aDomain)
    {
        domain = aDomain;
        return this;
    }
    
    public TemplateDomain build()
    {
        TemplateDomain templateDomain = new TemplateDomain();
        templateDomain.setEventTemplate(template);
        templateDomain.setDomain(domain);
        return templateDomain;
    }
}