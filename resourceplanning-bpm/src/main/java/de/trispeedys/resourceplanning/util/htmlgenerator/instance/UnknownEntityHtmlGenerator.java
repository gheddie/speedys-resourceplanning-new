package de.trispeedys.resourceplanning.util.htmlgenerator.instance;

import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class UnknownEntityHtmlGenerator extends HtmlGenerator
{
    public UnknownEntityHtmlGenerator(String message)
    {
        // message is already translated...
        withImage("speedys", "gif", 600, 170).withParagraph(message);
    }
}