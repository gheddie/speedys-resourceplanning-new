package de.trispeedys.resourceplanning;

import java.text.MessageFormat;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class BusinessKeys
{
    private static final String BK_REQUEST_HELP_HELPERPROCESS_TEMPLATE =
            "bkRequestHelpHelperProcess_helper:{0}||event:{1}";
    
    private static final String BK_SWAP_HELPERPROCESS_TEMPLATE =
            "bkRequestHelpHelperProcess_pos1:{0}||pos2:{1}||event:{2}";
    
    public static String generateRequestHelpBusinessKey(Helper helper, Event event)
    {
        if ((helper == null) || (event == null))
        {
            throw new ResourcePlanningException(
                    "helper AND event must be set in order to generate a business key!!");
        }
        return generateRequestHelpBusinessKey(helper.getId(), event.getId());
    }
    
    public static String generateRequestHelpBusinessKey(Long helperÎd, Long eventId)
    {
        if ((helperÎd == null) || (eventId == null))
        {
            throw new ResourcePlanningException(
                    "helper id AND event id must be set in order to generate a business key!!");
        }
        String businessKey = new MessageFormat(BK_REQUEST_HELP_HELPERPROCESS_TEMPLATE).format(new Object[]
        {
                String.valueOf(helperÎd), String.valueOf(eventId)
        });
        return businessKey;
    }
    
    public static String generateSwapBusinessKey(Event event, Position positionSource, Position positionTarget)
    {
        if ((event == null) || (positionSource == null) || (positionTarget == null))
        {
            throw new ResourcePlanningException(
                    "helper AND both positions must be set in order to generate a business key!!");
        }
        return generateSwapBusinessKey(event.getId(), positionSource.getId(), positionTarget.getId());
    }

    public static String generateSwapBusinessKey(Long eventId, Long positionIdSource, Long positionIdTarget)
    {
        if ((positionIdSource == null) || (positionIdTarget == null) || (eventId == null))
        {
            throw new ResourcePlanningException(
                    "helper id AND event id must be set in order to generate a business key!!");
        }
        String businessKey = new MessageFormat(BK_SWAP_HELPERPROCESS_TEMPLATE).format(new Object[]
        {
                String.valueOf(positionIdSource), String.valueOf(positionIdTarget), String.valueOf(eventId)
        });
        return businessKey;
    }
}