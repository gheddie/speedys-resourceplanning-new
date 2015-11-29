package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.HelperHistoryDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperHistory;
import de.trispeedys.resourceplanning.entity.misc.HistoryType;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class HelperHistoryRepository extends AbstractDatabaseRepository<HelperHistory> implements DatabaseRepository<HelperHistoryRepository>
{
    protected DefaultDatasource<HelperHistory> createDataSource()
    {
        return new HelperHistoryDatasource();
    }

    public List<HelperHistory> findOrdered(Helper helper, Event event)
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("helper", helper);
        variables.put("event", event);
        return dataSource().find(
                "FROM " + HelperHistory.class.getSimpleName() + " hh WHERE hh.helper = :helper AND hh.event = :event ORDER BY hh.creationTime ASC", variables);
    }

    public HelperHistory createEntry(Helper helper, Event event, HistoryType historyType)
    {
        return EntityFactory.buildHelperHistory(helper, event, historyType).saveOrUpdate();
    }

    public HelperHistory createEntry(Long helperId, Long eventId, HistoryType historyType)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        return createEntry(helper, event, historyType);
    }
}