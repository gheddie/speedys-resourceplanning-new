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
import de.trispeedys.resourceplanning.rule.CallbackChoiceGenerator;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class SendReminderMailTemplate extends HelperInteractionMailTemplate
{
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    private boolean priorPositionAvailable;

    private int attemptCount;

    public SendReminderMailTemplate(Helper helper, Event event, Position position, boolean aPriorPositionAvailable, int anAttemptCount)
    {
        super(helper, event, position);
        this.priorPositionAvailable = aPriorPositionAvailable;
        this.attemptCount = anAttemptCount;
    }

    public String constructBody()
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        HtmlGenerator generator =
                new HtmlGenerator(true).withParagraph(helperGreeting()).withParagraph(
                        configuration.getText(this, "priorAssignment", getPosition().getDescription(), getPosition().getDomain().getName()));
        if (!(priorPositionAvailable))
        {
            generator = generator.withParagraph(configuration.getText(this, "noLongerAvailable"));
        }
        else
        {
            generator = generator.withParagraph(configuration.getText(this, "available"));
        }
        generator = generator.withParagraph(configuration.getText(this, "body3", df.format(getEvent().getEventDate()), getEvent().getDescription()));
        List<HelperCallback> generated = new CallbackChoiceGenerator().generate(getHelper(), getEvent());
        if (generated != null)
        {
            for (HelperCallback callback : generated)
            {
                generator =
                        generator.withLink(
                                getBaseLink() +
                                        "/" + getJspReceiverName() + ".jsp?callbackResult=" + callback + "&helperId=" + getHelper().getId() + "&eventId=" + getEvent().getId() + "&priorPositionId=" +
                                        getPosition().getId(), callback.getDescription()).withLinebreak(2);
            }
        }
        generator = generator.withParagraph(sincerely());
        return generator.render();
    }

    public String constructSubject()
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        String subject = null;
        switch (attemptCount)
        {
            case 0:
                subject = configuration.getText(this, "subjectPlain", getEvent().getDescription());
                break;
            default:
                subject = configuration.getText(this, "subjectReminded", getEvent().getDescription(), attemptCount);
                break;
        }
        return subject;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.REMINDER_STEP_0;
    }

    protected String getJspReceiverName()
    {
        return "HelperCallbackReceiver";
    }
}