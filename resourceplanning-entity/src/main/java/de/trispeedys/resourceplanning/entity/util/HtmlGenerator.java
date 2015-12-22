package de.trispeedys.resourceplanning.entity.util;

import java.text.MessageFormat;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;

public class HtmlGenerator
{
    public static final String MACHINE_MESSAGE = "machineMessage";
    
    private StringBuffer buffer;

    private boolean renderNoReply;

    public HtmlGenerator(boolean aRenderNoReply)
    {
        super();
        this.renderNoReply = aRenderNoReply;
        this.buffer = new StringBuffer();
    }

    public HtmlGenerator()
    {
        this(false);
    }

    /**
     * referred to images must be placed in the /src/main/webapp/img directory.
     * 
     * @param filename
     * @param suffix
     * @param width 
     * @param height 
     * 
     * @return
     */
    public HtmlGenerator withImage(String filename, String suffix, int width, int height)
    {
        MessageFormat mf = new MessageFormat("<img src=\"img/{0}.{1}\" width=\"{2}\" height=\"{3}\" align=\"middle\">");
        buffer.append(mf.format(new Object[]
        {
                filename, suffix, width, height
        }));
        newLine();
        return this;
    }

    public HtmlGenerator withHeader(String text)
    {
        buffer.append("<h1>" + text + "</h1>");
        newLine();
        return this;
    }

    public HtmlGenerator withLinebreak()
    {
        return withLinebreak(1);
    }

    public HtmlGenerator withLinebreak(int howMany)
    {
        for (int i = 0; i < howMany; i++)
        {
            buffer.append("<br>");
            newLine();
        }
        return this;
    }
    
    public HtmlGenerator withListItem(String key)
    {
        buffer.append("<li>" + key + "</li>");
        newLine();
        return this;
    }
    
    public HtmlGenerator withUnorderedListEntry(String link, String text, boolean renderAsLink)
    {
        if (renderAsLink)
        {
            buffer.append("<ul><a href=\"" + link + "\">" + text + "</a></ul>");   
        }        
        else
        {
            // no link, just the entry...
            buffer.append("<ul>" + text + "</ul>");
        }
        newLine();
        return this;        
    }

    public HtmlGenerator withParagraph(String text)
    {
        buffer.append("<p>" + text + "</p>");
        newLine();
        return this;
    }
    
    public HtmlGenerator withClosingLink()
    {
        buffer.append("<a href=\"javascript:close_window();\">"+AppConfiguration.getInstance().getText(this, "close")+"</a>");
        newLine();
        return this;
    }
    
    public HtmlGenerator withLink(String link, String displayText)
    {
        MessageFormat mf = new MessageFormat("<a href=\"{0}\">{1}</a>");
        buffer.append(mf.format(new Object[]
        {
                link, displayText
        }));
        newLine();
        return this;
    }
    
    private void withMachineMessage()
    {
        withParagraph(AppConfiguration.getInstance().getText(HtmlGenerator.MACHINE_MESSAGE));
    }

    private void newLine()
    {
        buffer.append("\n");
    }

    public String render()
    {   
        if (renderNoReply)
        {
            withMachineMessage();   
        }        
        return buffer.toString();
    }
}