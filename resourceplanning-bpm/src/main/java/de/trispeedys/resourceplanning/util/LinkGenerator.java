package de.trispeedys.resourceplanning.util;

import java.text.MessageFormat;
import java.util.HashMap;

public class LinkGenerator
{
    private static final String FORMAT_WITH_PARAMETERS = "{0}/{1}?{2}";
    
    private static final String FORMAT_WITHOUT_PARAMETERS = "{0}/{1}";

    private String baseLink;
    
    private String receiverPageName;
    
    private HashMap<String, Object> parameters;

    public LinkGenerator(String baseLink, String receiverPageName, HashMap<String, Object> parameters)
    {
        super();
        this.baseLink = baseLink;
        this.receiverPageName = receiverPageName;
        this.parameters = parameters;
    }

    public String generate()
    {
        MessageFormat mf = null;
        if ((parameters == null) || (parameters.size() == 0))
        {
            mf = new MessageFormat(FORMAT_WITHOUT_PARAMETERS);  
            return mf.format(new Object[] {baseLink, receiverPageName});
        }
        else
        {
            mf = new MessageFormat(FORMAT_WITH_PARAMETERS);  
            return mf.format(new Object[] {baseLink, receiverPageName, concatedParameters()});
        }
    }

    private String concatedParameters()
    {
        String result = "";
        int index = 0;
        for (String parameterName : parameters.keySet())
        {
            result += parameterName + "=" + parameters.get(parameterName);
            
            if (index < parameters.size() - 1)
            {
                result += "&";
            }
            index++;
        }
        return result;
    }
}