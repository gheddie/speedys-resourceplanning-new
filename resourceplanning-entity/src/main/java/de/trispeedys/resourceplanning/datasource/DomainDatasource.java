package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.Domain;

public class DomainDatasource extends DefaultDatasource<Domain>
{
    protected Class<Domain> getGenericType()
    {
        return Domain.class;
    }
}