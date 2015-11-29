package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class AssignmentService
{
    public static void assignHelper(Helper helper, Event event, Position position)
            throws ResourcePlanningException
    {
        if (!(helper.isAssignableTo(position, event.getEventDate())))
        {
            throw new ResourcePlanningException("helper is to young for this position!");
        }
        EntityFactory.buildHelperAssignment(helper, event, position).saveOrUpdate();
    }

    public static boolean isFirstAssignment(Long helperId)
    {
        List<HelperAssignment> helperAssignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findAllHelperAssignments(helperId);
        return ((helperAssignments == null) || (helperAssignments.size() == 0));
    }

    /**
     * Sets a {@link HelperAssignment} to state {@link HelperAssignmentState#CANCELLED}.
     */
    public static void cancelHelperAssignment(Helper helper, Event event)
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        repository.updateSingleValue(repository.findByHelperAndEvent(helper, event), HelperAssignment.ATTR_ASSIGNMENT_STATE, HelperAssignmentState.CANCELLED);
    }
    
    /**
     * Sets a {@link HelperAssignment} to state {@link HelperAssignmentState#CONFIRMED}.
     */
    public static void confirmHelperAssignment(Helper helper, Event event)
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        repository.updateSingleValue(repository.findByHelperAndEvent(helper, event), HelperAssignment.ATTR_ASSIGNMENT_STATE, HelperAssignmentState.CONFIRMED);
    }

    public static HelperAssignment getPriorAssignment(Helper helper, EventTemplate eventTemplate)
    {
        String queryString =
                "From " +
                        HelperAssignment.class.getSimpleName() +
                        " ha INNER JOIN ha.event ev WHERE ha.helperId = :helperId AND ev.eventTemplate = :eventTemplate ORDER BY ev.eventDate DESC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helperId", helper.getId());
        parameters.put("eventTemplate", eventTemplate);
        List<Object[]> list = Datasources.getDatasource(HelperAssignment.class).find(queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        return (HelperAssignment) list.get(0)[0];
    }
}