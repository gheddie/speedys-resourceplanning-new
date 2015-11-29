package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class RepositoryProviderTest
{
    @Test
    public void testBasics()
    {
        HibernateUtil.clearAll();
        
        // build domain
        Domain domain = EntityFactory.buildDomain("D1", 1).saveOrUpdate();
        
        // build position
        Position pos = EntityFactory.buildPosition("P1", 12, domain, 0, true).saveOrUpdate();
        
        assertTrue(RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber(0) != null);
    }
}