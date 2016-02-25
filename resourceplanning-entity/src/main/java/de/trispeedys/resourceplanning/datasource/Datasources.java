package de.trispeedys.resourceplanning.datasource;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.util.xml.XmlReader;

public class Datasources
{
    private static final Logger logger = Logger.getLogger(Datasources.class);

    private static final String DATABASE_MAPPINGS_FILE_NAME = "database-mappings.xml";

    private static final String DATABASE_MAPPINGS_NODE_NAME = "database-mapping";

    private static final String DATABASE_MAPPINGS_ATTR_ENTITY_CLASS = "entity-class";

    private static final String DATABASE_MAPPINGS_ATTR_DB_CLASS = "datasource-class";

    private static Datasources instance;

    private HashMap<Class<? extends AbstractDbObject>, DefaultDatasource> registeredDatasources;

    private DefaultDatasource defaultDatasource;

    private Datasources()
    {
        registeredDatasources = new HashMap<Class<? extends AbstractDbObject>, DefaultDatasource>();
        registerDatasources();
        defaultDatasource = new DefaultDatasource();
    }

    private void registerDatasources()
    {
        logger.info("parsing configuration...");
        try
        {
            Document doc = XmlReader.readXml(getClass().getClassLoader().getResourceAsStream(DATABASE_MAPPINGS_FILE_NAME));
            NodeList nodeList = doc.getElementsByTagName(DATABASE_MAPPINGS_NODE_NAME);
            Node node = null;
            for (int index = 0; index < nodeList.getLength(); index++)
            {
                node = nodeList.item(index);
                registerDatasource(node.getAttributes().getNamedItem(DATABASE_MAPPINGS_ATTR_ENTITY_CLASS).getTextContent(),
                        node.getAttributes().getNamedItem(DATABASE_MAPPINGS_ATTR_DB_CLASS).getTextContent());
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
    }

    @SuppressWarnings(
    {
            "rawtypes", "unchecked"
    })
    private void registerDatasource(String entityClassName, String datasourceClassName)
    {
        Class<? extends AbstractDbObject> entityClass = null;
        DefaultDatasource datasourceInstance = null;
        try
        {
            entityClass = (Class<? extends AbstractDbObject>) Class.forName(entityClassName);
            datasourceInstance = (DefaultDatasource) Class.forName(datasourceClassName).newInstance();
            registeredDatasources.put(entityClass, datasourceInstance);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("unable to build a datasource for class : '" + entityClass + "'", e);
        }
    }

    private static Datasources getInstance()
    {
        if (Datasources.instance == null)
        {
            Datasources.instance = new Datasources();
        }
        return Datasources.instance;
    }

    @SuppressWarnings("unchecked")
    private <T> DefaultDatasource<T> datasource(Class<T> entityClass)
    {
        DefaultDatasource<T> dataSource = registeredDatasources.get(entityClass);
        return (dataSource != null
                ? dataSource
                : defaultDatasource);
    }

    public static <T> DefaultDatasource<T> getDatasource(Class<T> entityClass)
    {
        return getInstance().datasource(entityClass);
    }
}