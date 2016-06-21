package de.trispeedys.resourceplanning;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;

public class TriggerTest
{
    // @Test
    public void testTriggers()
    {
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).saveOrUpdate(new HelperAssignment());
    }
}