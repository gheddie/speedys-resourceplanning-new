package de.trispeedys.resourceplanning.messaging;

import java.util.HashMap;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.util.LinkGenerator;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class TriggerSimpleSwapMailTemplate extends SwapMailTemplate
{
    private Position targetPosition;

    public TriggerSimpleSwapMailTemplate(GuidedEvent event, HelperAssignment sourceAssignment, Position targetPosition)
    {
        super(event, sourceAssignment);
        this.targetPosition = targetPosition;
    }

    public String constructBody()
    {
        Position sourcePosition = getSourceAssignment().getPosition();
        Helper helperSource = getSourceAssignment().getHelper();
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(helperGreeting(helperSource))
                .withParagraph(
                        configuration.getText(SwapMailTemplate.class, "body", sourcePosition.getDescription(), sourcePosition.getDomain().getName(), targetPosition.getDescription(),
                                targetPosition.getDomain().getName()))
                .withLink(createLink(true), configuration.getText(SwapMailTemplate.class, "swapOk"))
                .withLinebreak(2)
                .withLink(createLink(false), configuration.getText(SwapMailTemplate.class, "swapNotOk"))
                .withParagraph(sincerely())
                .render();
    }

    private String createLink(boolean swapOk)
    {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("eventId", getEvent().getId());
        parameters.put("positionIdSource", getSourceAssignment().getPosition().getId());
        parameters.put("positionIdTarget", targetPosition.getId());
        parameters.put("swapOk", swapOk);
        return new LinkGenerator(getBaseLink(), "SimpleSwapConfirm.jsp", parameters).generate();
    }

    public String constructSubject()
    {
        return "Anfrage Positionstausch";
    }

    public MessagingType getMessagingType()
    {
        return null;
    }
}