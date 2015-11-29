package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.DomainDatasource;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;

public class DomainRepository extends AbstractDatabaseRepository<Domain> implements DatabaseRepository<DomainRepository>
{
    public Domain findDomainByNumber(int domainNumber)
    {
        return dataSource().findSingle(Domain.ATTR_DOMAIN_NUMBER, domainNumber);
    }

    protected DefaultDatasource<Domain> createDataSource()
    {
        return new DomainDatasource();
    }
}