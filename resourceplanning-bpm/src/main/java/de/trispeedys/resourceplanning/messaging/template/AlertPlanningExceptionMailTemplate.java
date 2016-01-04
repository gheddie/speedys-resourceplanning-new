package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;

public class AlertPlanningExceptionMailTemplate extends AbstractMailTemplate
{
    private String errorMessage;

    public AlertPlanningExceptionMailTemplate(Helper helper, Event event, Position position, String anErrorMessage)
    {
        super(helper, event, position);
        this.errorMessage = anErrorMessage;
    }

    public String constructBody()
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(configuration.getText(this, "adminSalutation"))
                .withParagraph(
                        configuration.getText(this, "body", getHelper().getLastName(), getHelper().getFirstName(),
                                errorMessage))
                .render();
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject", getEvent().getDescription());
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return null;
    }
}