package de.trispeedys.resourceplanning.util;

import java.util.List;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

public class PositionTreeNode<T> extends EntityTreeNode<Position>
{
    public PositionTreeNode()
    {
        super();
    }

    public Object getHierarchicalItem()
    {
        return ((AssignmentContainer) getPayLoad()).getPosition();
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_POSITION;
    }

    public String infoString()
    {
        return ((AssignmentContainer) getPayLoad()).getPosition().getDescription();
    }

    public HierarchicalEventItemType getItemType()
    {
        return HierarchicalEventItemType.POSITION;
    }

    public String itemKey()
    {
        Position eventItem = ((AssignmentContainer) getPayLoad()).getPosition();
        return eventItem.getDifferentiator();
    }

    public String getAssignmentString()
    {
        Helper helper = ((AssignmentContainer) getPayLoad()).getHelper();
        if (helper != null)
        {
            return helper.getLastName() + ", " + helper.getFirstName();
        }
        else
        {
            return "[--N/A--]";
        }
    }

    public String getPriorization()
    {
        Position position = ((AssignmentContainer) getPayLoad()).getPosition();
        if (position != null)
        {
            return (position.getAssignmentPriority() != null
                    ? String.valueOf(position.getAssignmentPriority())
                    : "");
        }
        else
        {
            return "";
        }
    }
    
    public String getAvailability(List<?> referencePositions, Object parent)
    {
        Event event = (Event) parent;
        if (!(event.getEventState().equals(EventState.INITIATED)))
        {
            return "";
        }
        Position position = ((AssignmentContainer) getPayLoad()).getPosition();
        if (position != null)
        {
            if (!(position.isChoosable()))
            {
                // nie wählbar
                return "NIE";
            }
            else
            {
                if (referencePositions.contains(position))
                {
                    // aktuell wählbar
                    return "JA";
                }
                else
                {
                    // noch nicht wählbar (Gruppierung)
                    return "BLOCKIERT";
                }
            }
        }
        else
        {
            return "";
        }
    }

    public Long getEntityId()
    {
        Helper helper = ((AssignmentContainer) getPayLoad()).getHelper();
        return (helper != null
                ? helper.getId()
                : null);
    }

    public Long getPositionId()
    {
        Position position = ((AssignmentContainer) getPayLoad()).getPosition();
        return (position != null
                ? position.getId()
                : null);
    }
}