package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.DomainResponsibility;
import de.trispeedys.resourceplanning.entity.Helper;

public class DomainResponsibilityBuilder extends AbstractEntityBuilder<DomainResponsibility>
{
    private Helper helper;
    
    private Domain domain;

    public DomainResponsibilityBuilder withDomain(Domain aDomain)
    {
        domain = aDomain;
        return this;
    }

    public DomainResponsibilityBuilder withHelper(Helper aHelper)
    {
        helper = aHelper;
        return this;
    }
    
    public DomainResponsibility build()
    {
        DomainResponsibility domainResponsibility = new DomainResponsibility();
        domainResponsibility.setDomain(domain);
        domainResponsibility.setHelper(helper);
        return domainResponsibility;
    }    
}