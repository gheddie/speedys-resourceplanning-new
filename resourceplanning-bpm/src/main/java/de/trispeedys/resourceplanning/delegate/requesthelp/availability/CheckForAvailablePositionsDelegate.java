package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.PositionRepository;

public class CheckForAvailablePositionsDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // look up choosable positions via 'ChoosablePositionGenerator', because it is used in following delegate
        // code ('ProposePositionsDelegate'), too...
        List<Position> unassignedPositions =
                RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsByGenerator(getHelper(execution), getEvent(execution));        
        // Wenn keine Positionen zum Vorschlagen da sind, zur manuellen Zuweisung abbiegen...
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_POSITIONS_PROPOSABLE,
                ((unassignedPositions != null) && (unassignedPositions.size() > 0)));
    }
}