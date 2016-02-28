package de.gravitex.hibernateadapter.core.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.util.xml.XmlReader;

public class RepositoryProvider
{
    private static final Logger logger = Logger.getLogger(RepositoryProvider.class);
    
    private static final String REPO_CONFIG_FILE_NAME = "repository-definitions.xml";

    private static final String REPO_CONFIG_NODE_NAME = "repository-class";

    private static RepositoryProvider instance;
    
    @SuppressWarnings("rawtypes")
    private HashMap<Class<? extends DatabaseRepository>, DatabaseRepository> repositoryCache;
    
    @SuppressWarnings("rawtypes")
    private RepositoryProvider()
    {
        repositoryCache = new HashMap<Class<? extends DatabaseRepository>, DatabaseRepository>();
        parseRepositoryConfiguration();
    }

    private void parseRepositoryConfiguration()
    {
        // TODO improve exception handling
        
        logger.info("parsing configuration...");
        try
        {
            Document doc =
                    XmlReader.readXml(getClass().getClassLoader().getResourceAsStream(
                            REPO_CONFIG_FILE_NAME));
            ArrayList<String> repositoryClassNames = new ArrayList<String>();
            NodeList nodeList = doc.getElementsByTagName(REPO_CONFIG_NODE_NAME);
            Node node = null;
            for (int index = 0; index < nodeList.getLength(); index++)
            {
                node = nodeList.item(index);
                repositoryClassNames.add(node.getTextContent());
            }
            for (String clazz : repositoryClassNames)
            {
                registerRepository((Class<? extends DatabaseRepository>) Class.forName(clazz));
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
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    private void registerRepository(Class<? extends DatabaseRepository> clazz)
    {
        try
        {
            DatabaseRepository repositoryInstance = clazz.newInstance();
            repositoryCache.put(clazz, repositoryInstance);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }        
    }

    private static RepositoryProvider getInstance()
    {
        if (RepositoryProvider.instance == null)
        {
            RepositoryProvider.instance = new RepositoryProvider();
        }
        return RepositoryProvider.instance;
    }
    
    @SuppressWarnings("unchecked")
    private <T extends DatabaseRepository<T>> T getRepositoryForClass(Class<? extends AbstractDbObject> entityClass)
    {
        if (entityClass == null)
        {
            return null;
        }
        return (T) repositoryCache.get(entityClass);        
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends DatabaseRepository<T>> T getRepository(Class<T> repositoryClass)
    {
        T repositoryForClass = (T) getInstance().getRepositoryForClass((Class<? extends AbstractDbObject>) repositoryClass);        
        return repositoryForClass;
    }    
}