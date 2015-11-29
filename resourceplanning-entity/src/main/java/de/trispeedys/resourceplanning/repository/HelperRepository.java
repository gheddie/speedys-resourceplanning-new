package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.HelperDatasource;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;

public class HelperRepository extends AbstractDatabaseRepository<Helper> implements DatabaseRepository<HelperRepository>
{
    protected DefaultDatasource<Helper> createDataSource()
    {
        return new HelperDatasource();
    }

    public Helper findByCode(String helperCode)
    {
        return dataSource().findSingle(Helper.ATTR_CODE, helperCode);
    }

    public List<Helper> findActiveHelpers()
    {
        return dataSource().find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
    }
}