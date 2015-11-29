package de.trispeedys.resourceplanning.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class ProposePositionsMailTemplate extends AbstractMailTemplate
{
    private List<Position> positions;

    // which kind of callback caused this mail?
    private HelperCallback trigger;

    private Position priorAssignment;

    // set if this mail is triggered because a former chosen position is already gone
    private boolean reentrant;

    public ProposePositionsMailTemplate(Helper aHelper, Event aEvent, List<Position> aPositions, HelperCallback aTrigger,
            Position aPriorAssignment, boolean aReentrant)
    {
        super(aHelper, aEvent, aPriorAssignment);
        this.positions = aPositions;
        this.trigger = aTrigger;
        this.priorAssignment = aPriorAssignment;
        this.reentrant = aReentrant;
    }

    public String constructBody()
    {
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
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hallo, " + getHelper().getFirstName() + "!!");
        buffer.append("<br><br>");
        if (reentrant)
        {
            // reentrant TODO welche zuvor gewählte Position?
            buffer.append("Deine zuvor gewählte Position ist leider bereits besetzt.");
            buffer.append("<br><br>");            
        }
        else
        {
            // not reentrant --> assignment as before not possible
            if (trigger.equals(HelperCallback.ASSIGNMENT_AS_BEFORE))
            {
                buffer.append("Deine vormalige Position (" +
                        priorAssignment.getDescription() + " im Bereich " + priorAssignment.getDomain().getName() +
                        ") ist leider bereits besetzt.");
                buffer.append("<br><br>");
            }   
        }
        buffer.append("Bitte sag uns, welche Position du beim " + getEvent().getDescription() + " besetzen möchtest:");
        buffer.append("<br><br>");
        String entry = null;
        for (String key : grouping.keySet())
        {
            buffer.append("<li>" + key + "</li>");
            for (Position pos : grouping.get(key))
            {
                entry =
                        HelperInteraction.getBaseLink() +
                                "/ChosenPositionReceiver.jsp?chosenPosition=" + pos.getId() + "&helperId=" + getHelper().getId() +
                                "&eventId=" + getEvent().getId();
                buffer.append("<ul><a href=\"" + entry + "\">" + pos.getDescription() + "</a></ul>");
            }
        }
        buffer.append("<br>");
        buffer.append("Deine Speedys");
        buffer.append("<br><br>");
        buffer.append(HtmlGenerator.MACHINE_MESSAGE);        
        return buffer.toString();
    }

    public String constructSubject()
    {
        return "Positions-Auswahl für den Wettkampf " + getEvent().getDescription();
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.PROPOSE_POSITIONS;
    }
}