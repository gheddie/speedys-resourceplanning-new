package de.trispeedys.resourceplanning.messaging.template.helprequest;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.messaging.RequestHelpMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class AlertPlanningExceptionMailTemplate extends RequestHelpMailTemplate
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
        Helper helper = getHelper();
        if (helper != null)
        {
            return new HtmlGenerator(true).withParagraph(configuration.getText(this, "adminSalutation"))
                    .withParagraph(
                            configuration.getText(this, "body", helper.getLastName(), helper.getFirstName(),
                                    errorMessage))
                    .render();   
        }
        else
        {
            // TODO may be we should render a better body here...
            return new HtmlGenerator(true).withParagraph(configuration.getText(this, "adminSalutation"))
                    .withParagraph(
                            configuration.getText(this, "bodyPlain", errorMessage))
                    .render();   
        }
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject", getEvent().getDescription());
    }

    public MessagingType getMessagingType()
    {
        return null;
    }
}