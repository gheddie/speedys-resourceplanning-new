package de.trispeedys.resourceplanning.rule;

import java.util.Arrays;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class CallbackChoiceGenerator extends RuleObject<HelperCallback>
{
    public List<HelperCallback> generate(Helper helper, Event event)
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
                    HelperCallback.CHANGE_POS, HelperCallback.PAUSE_ME, HelperCallback.ASSIGN_ME_MANUALLY
            });
        }
        return Arrays.asList(HelperCallback.values());
    }
}