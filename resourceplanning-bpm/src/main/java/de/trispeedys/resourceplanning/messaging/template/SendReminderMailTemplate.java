package de.trispeedys.resourceplanning.messaging.template;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.rule.CallbackChoiceGenerator;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class SendReminderMailTemplate extends AbstractMailTemplate
{
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    private boolean priorPositionAvailable;

    private int attemptCount;

    public SendReminderMailTemplate(Helper helper, Event event, Position position, boolean aPriorPositionAvailable,
            int anAttemptCount)
    {
        super(helper, event, position);
        this.priorPositionAvailable = aPriorPositionAvailable;
        this.attemptCount = anAttemptCount;
    }

    public String constructBody()
    {
        HtmlGenerator generator =
                new HtmlGenerator(true).withParagraph(helperGreeting()).withParagraph(
                        AppConfiguration.getInstance().getText(this, "priorAssignment", getPosition().getDescription(),
                                getPosition().getDomain().getName()));
        if (!(priorPositionAvailable))
        {
            generator = generator.withParagraph("Diese Position ist leider nicht mehr verfügbar.");
        }
        else
        {
            generator = generator.withParagraph("Diese Position auch dieses Mal wieder zu besetzen.");
        }
        generator =
                generator.withParagraph(AppConfiguration.getInstance().getText(this, "body3",
                        df.format(getEvent().getEventDate()), getEvent().getDescription()));
        List<HelperCallback> generated = new CallbackChoiceGenerator().generate(getHelper(), getEvent());
        if (generated != null)
        {
            for (HelperCallback callback : generated)
            {
                generator =
                        generator.withLink(
                                HelperInteraction.getBaseLink() +
                                        "/HelperCallbackReceiver.jsp?callbackResult=" + callback + "&helperId=" +
                                        getHelper().getId() + "&eventId=" + getEvent().getId(),
                                callback.getDescription()).withLinebreak(2);
            }
        }
        generator = generator.withParagraph("Deine Tri-Speedys.");
        return generator.render();
    }

    public String constructSubject()
    {
        String subject = null;
        switch (attemptCount)
        {
            case 0:
                subject = "Helfermeldung zum " + getEvent().getDescription();
                break;
            default:
                subject = "Helfermeldung zum " + getEvent().getDescription() + " (" + attemptCount + ". Erinnerung)";
                break;
        }
        return subject;
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.REMINDER_STEP_0;
    }
}