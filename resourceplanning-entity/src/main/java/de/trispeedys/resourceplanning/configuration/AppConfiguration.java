package de.trispeedys.resourceplanning.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.entity.AppConfigurationEntry;
import de.trispeedys.resourceplanning.repository.AppConfigurationEntryRepository;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.xml.XmlReader;

public class AppConfiguration
{
    private static final Logger logger = Logger.getLogger(AppConfiguration.class);

    private static final String PROPERTIES_NAME = "messages.properties";

    private static final boolean READ_CONF_FROM_XML = false;

    private HashMap<String, String> configurationValues;

    private static AppConfiguration instance;

    private Properties textResources;

    private boolean planningInProgress;

    private boolean mailSendingInProgress;

    private AppConfiguration()
    {
        planningInProgress = false;
        mailSendingInProgress = false;
        parseConfiguration();
        initTextResources();
    }

    private void initTextResources()
    {
        logger.info("initializing text resources...");

        textResources = new Properties();
        try
        {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_NAME);
            if (stream != null)
            {
                textResources.load(stream);   
            }            
        }
        catch (IOException e)
        {
            logger.error(e);
        }
    }

    public void parseConfiguration()
    {
        if (READ_CONF_FROM_XML)
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
                // TODO ...
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
        else
        {
            // read config from database...
            configurationValues = new HashMap<String, String>();
            for (AppConfigurationEntry entry : RepositoryProvider.getRepository(AppConfigurationEntryRepository.class).findAll())
            {
                configurationValues.put(entry.getKey(), entry.getValue());
            }
        }
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
    
    public void setConfigurationValue(String key, String value)
    {
        configurationValues.put(key, value);
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
            return " ### " + key + " ### ";
        }
        logger.debug("got text resource for key [" + key + "] : " + resource);
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

    public boolean isMailSendingInProgress()
    {
        return mailSendingInProgress;
    }
    
    public void setMailSendingInProgress(boolean mailSendingInProgress)
    {
        this.mailSendingInProgress = mailSendingInProgress;
    }
}