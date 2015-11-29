package de.trispeedys.resourceplanning.util.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.trispeedys.resourceplanning.util.XmlReader;

public class AppConfiguration
{       
    private HashMap<String, String> configurationValues;    

    private static AppConfiguration instance;

    private AppConfiguration()
    {
        parseConfiguration();
    }

    private void parseConfiguration()
    {
        try
        {
            Document doc = XmlReader.readXml(getClass().getClassLoader().getResourceAsStream(AppConfigurationValues.CONFIG_FILE_NAME));
            configurationValues = new HashMap<String, String>();
            NodeList nodeList = doc.getElementsByTagName(AppConfigurationValues.PROPERTY_NODE_NAME);
            Node node = null;
            for (int index = 0; index < nodeList.getLength(); index++)
            {
                node = nodeList.item(index);
                configurationValues.put(node.getAttributes().getNamedItem(AppConfigurationValues.CONF_ATTR_NAME).getTextContent(), node.getTextContent());
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
        //LoggerService.log("parsed configuration : " + resource, DbLogLevel.INFO);
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
}