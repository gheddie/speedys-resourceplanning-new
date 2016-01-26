package de.trispeedys.resourceplanning.messaging.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class ProposePositionsMailTemplate extends HelperInteractionMailTemplate
{
    private List<Position> positions;

    // which kind of callback caused this mail?
    private HelperCallback trigger;

    private Position priorAssignment;

    // set if this mail is triggered because a former chosen position is already gone
    private boolean reentrant;

    public ProposePositionsMailTemplate(Helper aHelper, Event aEvent, List<Position> aPositions,
            HelperCallback aTrigger, Position aPriorAssignment, boolean aReentrant)
    {
        super(aHelper, aEvent, aPriorAssignment);
        this.positions = aPositions;
        this.trigger = aTrigger;
        this.priorAssignment = aPriorAssignment;
        this.reentrant = aReentrant;
    }

    public String constructBody()
    {
        AppConfiguration configuration = AppConfiguration.getInstance();

        // group positions by domain (name)
        HashMap<String, List<Position>> grouping = new HashMap<String, List<Position>>();
        String domainName = null;
        for (Position pos : positions)
        {
            domainName = pos.getDomain().getName();
            if (grouping.get(domainName) == null)
            {
                grouping.put(domainName, new ArrayList<Position>());
            }
            grouping.get(domainName).add(pos);
        }

        HtmlGenerator generator = new HtmlGenerator(true);
        generator.withParagraph(helperGreeting());
        if (reentrant)
        {
            // reentrant TODO welche zuvor gewählte Position?
            generator.withParagraph(configuration.getText(this, "priorAssignmentBlockedReentrant"));
        }
        else
        {
            // not reentrant --> assignment as before not possible
            if (trigger.equals(HelperCallback.ASSIGNMENT_AS_BEFORE))
            {
                generator.withParagraph(configuration.getText(this, "priorAssignmentBlocked",
                        priorAssignment.getDescription(), priorAssignment.getDomain().getName()));
            }
        }
        generator.withParagraph(configuration.getText(this, "requestAssignment", getEvent().getDescription()));
        String link = null;
        for (String key : grouping.keySet())
        {
            generator.withListItem(key);
            for (Position pos : grouping.get(key))
            {
                link =
                        getBaseLink() +
                                "/"+getJspReceiverName()+".jsp?chosenPosition=" + pos.getId() + "&helperId=" +
                                getHelper().getId() + "&eventId=" + getEvent().getId();
                generator.withUnorderedListEntry(link, pos.getDescription(), true);
            }
        }
        generator.withParagraph(sincerely());
        return generator.render();
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject", getEvent().getDescription());
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.PROPOSE_POSITIONS;
    }
    
    protected String getJspReceiverName()
    {
        return "ChosenPositionReceiver";
    }
}