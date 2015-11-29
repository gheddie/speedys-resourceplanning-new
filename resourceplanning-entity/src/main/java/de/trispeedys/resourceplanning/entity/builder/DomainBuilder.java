package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Domain;

public class DomainBuilder extends AbstractEntityBuilder<Domain>
{
//    private Helper leader;
    
    private int domainNumber;
    
    private String name;

    /*
    public DomainBuilder withLeader(Helper aLeader)
    {
        leader = aLeader;
        return this;
    }
    */
    
    public DomainBuilder withName(String aName)
    {
        name = aName;
        return this;
    }
    
    public DomainBuilder withDomainNumber(int aDomainNumber)
    {
        domainNumber = aDomainNumber;
        return this;
    }    
    
    public Domain build()
    {
        Domain domain = new Domain();
//        domain.setLeader(leader);
        domain.setDomainNumber(domainNumber);
        domain.setName(name);
        return domain;
    }    
}