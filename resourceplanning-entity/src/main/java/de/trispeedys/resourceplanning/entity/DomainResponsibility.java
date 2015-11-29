package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "domain_responsibility")
public class DomainResponsibility extends AbstractDbObject
{
    @OneToOne
    @JoinColumn(name = "helper_id")
    private Helper helper;
    
    @OneToOne
    @JoinColumn(name = "domain_id")
    private Domain domain;

    public Helper getHelper()
    {
        return helper;
    }

    public void setHelper(Helper helper)
    {
        this.helper = helper;
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