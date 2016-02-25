package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;

@Entity
@Table(name = "template_domain")
public class TemplateDomain extends AbstractDbObject
{
    @OneToOne
    @JoinColumn(name = "event_template_id")
    private EventTemplate eventTemplate;
    
    @OneToOne
    @JoinColumn(name = "domain_id")
    private Domain domain;

    public EventTemplate getEventTemplate()
    {
        return eventTemplate;
    }
    
    public void setEventTemplate(EventTemplate eventTemplate)
    {
        this.eventTemplate = eventTemplate;
    }

    public Domain getDomain()
    {
        return domain;
    }

    public void setDomain(Domain domain)
    {
        this.domain = domain;
    }
}