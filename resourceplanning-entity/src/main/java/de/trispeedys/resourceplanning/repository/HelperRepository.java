package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.HelperDatasource;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class HelperRepository extends AbstractDatabaseRepository<Helper> implements DatabaseRepository<HelperRepository>
{
    protected DefaultDatasource<Helper> createDataSource()
    {
        return new HelperDatasource();
    }

    public Helper findByCode(String helperCode)
    {
        return dataSource().findSingle(null, Helper.ATTR_CODE, helperCode);
    }

    public List<Helper> findActiveHelpers()
    {
        return dataSource().find(null, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
    }
    
    public List<Long> queryActiveHelperIds()
    {
        List<Long> result = new ArrayList<Long>();
        for (Object helper : Datasources.getDatasource(Helper.class).find(null, "helperState",
                HelperState.ACTIVE))
        {
            result.add(((AbstractDbObject) helper).getId());
        }
        return result;
    }

    public void deactivateHelper(Long helperId)
    {
        HelperRepository repository = RepositoryProvider.getRepository(HelperRepository.class);
        Helper helper = repository.findById(helperId);
        repository.updateSingleValue(helper, Helper.ATTR_HELPER_STATE, HelperState.INACTIVE);
    }

    public boolean isHelperAssignedForPosition(Helper helper, Event event, Position position)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helper", helper);
        parameters.put("event", event);
        parameters.put("position", position);
        List<HelperAssignment> list =
                Datasources.getDatasource(Helper.class)
                        .find(null, "FROM " +
                                HelperAssignment.class.getSimpleName() +
                                " ec WHERE ec.helper = :helper AND ec.event = :event AND ec.position = :position",
                                parameters);
        return ((list != null) || (list.size() == 1));
    }

    public Position getHelperAssignment(Helper helper, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helper", helper);
        parameters.put("event", event);
        List<HelperAssignment> helperAssignments =
                Datasources.getDatasource(HelperAssignment.class).find(null, 
                        "FROM " +
                                HelperAssignment.class.getSimpleName() +
                                " ec WHERE ec.event = :event AND ec.helper = :helper", parameters);
        if ((helperAssignments == null) || (helperAssignments.size() == 0))
        {
            return null;
        }
        return helperAssignments.get(0).getPosition();
    }
}