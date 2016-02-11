package de.trispeedys.resourceplanning.delegate.swappositions;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class PostSwapResultDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        AppConfiguration configuration = AppConfiguration.getInstance();

        Position sourcePosition = getSourcePosition(execution);
        Position targetPosition = getTargetPosition(execution);
        AssignmentSwap swap = getSwapEntity(execution);
        // the recipients for this mail
        List<String> recipients = new ArrayList<>();
        String subject = configuration.getText(this, "mailSubject");
        String body = null;
        switch (swap.getSwapType())
        {
            case COMPLEX:
                // add both helpers as recipients...
                recipients.add(getSourceAssignment(execution).getHelper().getEmail());
                recipients.add(getTargetAssignment(execution).getHelper().getEmail());
                switch (swap.getSwapState())
                {
                    case INTERRUPTED:
                        body = configuration.getText(this, "swapInterrupted", sourcePosition.getDescription(), targetPosition.getDescription());
                        break;
                    case COMPLETED:
                        body = configuration.getText(this, "swapSuccesful", sourcePosition.getDescription(), targetPosition.getDescription());
                        break;
                    case REJECTED:
                        body = configuration.getText(this, "swapRejected", sourcePosition.getDescription(), targetPosition.getDescription());
                        break;
                    default:
                        break;
                }
                break;
            case SIMPLE:
                // add the 'source' helper as recipients only, because there is no target helper --> target position was unassigned...
                // TODO this is not correct...
                recipients.add(getSourceAssignment(execution) != null
                        ? getSourceAssignment(execution).getHelper().getEmail()
                        : getTargetAssignment(execution).getHelper().getEmail());
                switch (swap.getSwapState())
                {
                    case COMPLETED:
                        body = configuration.getText(this, "swapSuccesful", sourcePosition.getDescription(), targetPosition.getDescription());
                        break;
                    case INTERRUPTED:
                        break;
                    case REJECTED:
                        body = configuration.getText(this, "swapRejected", sourcePosition.getDescription(), targetPosition.getDescription());
                        break;
                    case REQUESTED:
                        break;
                    default:
                        break;
                }
                break;
        }
        
        // TODO use html generator, add sincerely and stuff...and be more precise in information...
        // tell helper something like 'you are now on position...' (and add the domain, too...)
        
        // the admin gets this mail...
        recipients.add(configuration.getConfigurationValue(AppConfigurationValues.ADMIN_MAIL));
        MessageQueueRepository messageQueueRepository = RepositoryProvider.getRepository(MessageQueueRepository.class);
        for (String recipient : recipients)
        {
            messageQueueRepository.createMessage("noreply@tri-speedys.de", recipient, subject, body, MessagingType.SWAP_RESULT, true, null);
        }
    }
}