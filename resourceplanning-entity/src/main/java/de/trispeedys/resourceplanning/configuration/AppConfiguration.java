package de.trispeedys.resourceplanning.configuration;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.util.xml.XmlReader;

public class AppConfiguration
{
    private static final Logger logger = Logger.getLogger(AppConfiguration.class);

    private static final String PROPERTIES_NAME = "messages.properties";

    private HashMap<String, String> configurationValues;

    private static AppConfiguration instance;

    private Properties textResources;

    private boolean planningInProgress;

    private AppConfiguration()
    {
        planningInProgress = false;
        parseConfiguration();
        initTextResources();
    }

    private void initTextResources()
    {
        logger.info("initializing text resources...");

        textResources = new Properties();
        try
        {
            textResources.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_NAME));
        }
        catch (IOException e)
        {
            logger.error(e);
        }
    }

    private void parseConfiguration()
    {
        logger.info("parsing configuration...");
        try
        {
            Document doc =
                    XmlReader.readXml(getClass().getClassLoader().getResourceAsStream(
                            AppConfigurationValues.CONFIG_FILE_NAME));
            configurationValues = new HashMap<String, String>();
            NodeList nodeList = doc.getElementsByTagName(AppConfigurationValues.PROPERTY_NODE_NAME);
            Node node = null;
            for (int index = 0; index < nodeList.getLength(); index++)
            {
                node = nodeList.item(index);
                configurationValues.put(node.getAttributes()
                        .getNamedItem(AppConfigurationValues.CONF_ATTR_NAME)
                        .getTextContent(), node.getTextContent());
            }
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        // LoggerService.log("parsed configuration : " + resource, DbLogLevel.INFO);
    }

    public static AppConfiguration getInstance()
    {
        if (AppConfiguration.instance == null)
        {
            AppConfiguration.instance = new AppConfiguration();
        }
        return AppConfiguration.instance;
    }

    public String getConfigurationValue(String key)
    {
        return configurationValues.get(key);
    }
    
    public String getText(Object context, String key, Object... parameters)
    {
        return getText(context.getClass(), key, parameters);
    }

    public String getText(Class<?> context, String key, Object... parameters)
    {
        String resource = context != null
                ? textResources.getProperty(context.getSimpleName() + "." + key)
                : textResources.getProperty(key);
        if (StringUtil.isBlank(resource))
        {
            return ">>>" + key + "<<<";
        }
        logger.info("got text resource for key [" + key + "] : " + resource);
        MessageFormat mf = new MessageFormat(resource);
        String result = mf.format(parameters);
        return result;
    }

    public String getText(String key)
    {
        return getText(null, key, null);
    }

    public boolean isPlanningInProgress()
    {
        return planningInProgress;
    }

    public void setPlanningInProgress(boolean planningInProgress)
    {
        this.planningInProgress = planningInProgress;
    }
}