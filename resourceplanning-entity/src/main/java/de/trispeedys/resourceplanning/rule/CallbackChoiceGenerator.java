package de.trispeedys.resourceplanning.rule;

import java.util.Arrays;
import java.util.List;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;

public class CallbackChoiceGenerator extends RuleObject<HelperCallback>
{
    public List<HelperCallback> generate(Helper helper, GuidedEvent event)
    {
        if (RepositoryProvider.getRepository(HelperAssignmentRepository.class).isFirstAssignment(helper.getId()))
        {
            return null;
        }
        Position priorPosition = RepositoryProvider.getRepository(HelperAssignmentRepository.class).getPriorAssignment(helper, event.getEventTemplate()).getPosition();
        if (!(RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(event, priorPosition)))
        {
            // prior position is not available, so...
            return Arrays.asList(new HelperCallback[]
            {
                    HelperCallback.CHANGE_POS, HelperCallback.PAUSE_ME, HelperCallback.ASSIGN_ME_MANUALLY, HelperCallback.QUIT_FOREVER
            });
        }
        return Arrays.asList(HelperCallback.values());
    }
}