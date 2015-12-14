package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

@SuppressWarnings("rawtypes")
public class PreplanInfoMailTemplate extends AbstractMailTemplate
{
    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph("Hallo " + getHelper().getFirstName() + "!")
                .withParagraph(
                        "Dein Einsatz auf der Position '" +
                                getPosition().getDescription() +
                                "' wurde in die abschliessende Event-Planung �bernommen.")
                .withParagraph(
                        "Deine Zusage kann nun nicht mehr �ber den Link in der Best�tigungs-Mail gek�ndigt werden.")
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public String constructSubject()
    {
        return "Abschluss der Planung";
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.PREPLAN_INFO;
    }
}