package de.trispeedys.resourceplanning.repository;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.DomainDatasource;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class DomainRepository extends AbstractDatabaseRepository<Domain> implements DatabaseRepository<DomainRepository>
{
    public Domain findDomainByNumber(int domainNumber)
    {
        return dataSource().findSingle(null, Domain.ATTR_DOMAIN_NUMBER, domainNumber);
    }

    protected DefaultDatasource<Domain> createDataSource()
    {
        return new DomainDatasource();
    }

    public Domain createDomain(String name, int domainNumber)
    {
        return EntityFactory.buildDomain(name, domainNumber).saveOrUpdate();
    }
}