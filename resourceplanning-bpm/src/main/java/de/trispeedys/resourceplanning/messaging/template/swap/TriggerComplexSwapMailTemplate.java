package de.trispeedys.resourceplanning.messaging.template.swap;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.messaging.SwapMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public abstract class TriggerComplexSwapMailTemplate extends SwapMailTemplate
{
    public static final String TRIGGER_SOURCE = "source";

    public static final String TRIGGER_TARGET = "target";
    
    private HelperAssignment targetAssignment;

    public TriggerComplexSwapMailTemplate(Event event, HelperAssignment sourceAssignment, HelperAssignment targetAssignment)
    {
        super(event, sourceAssignment);
        this.targetAssignment = targetAssignment;
    }

    public String constructBody()
    {
        Long eventId = getEvent().getId();
        Long positionIdSource = getSourceAssignment().getPosition().getId();
        Long positionIdTarget = getTargetAssignment().getPosition().getId();
        String jspReceiverName = "ComplexSwapConfirm";
        AppConfiguration configuration = AppConfiguration.getInstance();
        String linkYes =
                getBaseLink() +
                        "/" + jspReceiverName + ".jsp?eventId=" + eventId + "&positionIdSource=" + positionIdSource + "&positionIdTarget=" + positionIdTarget + "&swapOk=yes" + "&trigger=" + trigger();
        String linkNo =
                getBaseLink() +
                        "/" + jspReceiverName + ".jsp?eventId=" + eventId + "&positionIdSource=" + positionIdSource + "&positionIdTarget=" + positionIdTarget + "&swapOk=no" + "&trigger=" + trigger();
        return new HtmlGenerator(true).withParagraph(helperGreeting(relevantHelper()))
                .withParagraph(configureBodyText())
                .withLink(linkYes, configuration.getText(SwapMailTemplate.class, "swapOk"))
                .withLinebreak(2)
                .withLink(linkNo, configuration.getText(SwapMailTemplate.class, "swapNotOk"))
                .withParagraph(sincerely())
                .render();
    }

    protected abstract String configureBodyText();

    protected abstract Helper relevantHelper();

    protected abstract String trigger();

    public String constructSubject()
    {
        return "Anfrage Positionstausch";
    }

    public HelperAssignment getTargetAssignment()
    {
        return targetAssignment;
    }
}