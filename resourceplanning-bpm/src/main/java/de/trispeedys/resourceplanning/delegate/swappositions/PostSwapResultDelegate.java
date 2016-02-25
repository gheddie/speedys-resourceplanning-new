package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.SwapState;
import de.trispeedys.resourceplanning.entity.misc.SwapType;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class PostSwapResultDelegate extends AbstractSwapDelegate
{
    private static final String SWAP_EXECUTED = "swapExecuted";

    private static final String SWAP_ABORTED = "swapAborted";

    private AssignmentSwap swap;

    private boolean swapBySystem;

    public void execute(DelegateExecution execution) throws Exception
    {
        swap = getSwapEntity(execution);
        swapBySystem = (boolean) execution.getVariable(BpmVariables.Swap.VAR_SWAP_BY_SYSTEM);
        if (swap.getSwapType().equals(SwapType.SIMPLE))
        {
            processSimpleResult();
        }
        else
        {
            processComplexResult();
        }
    }

    private void processSimpleResult()
    {
        if (swap.getSwapState().equals(SwapState.COMPLETED))
        {
            // single swap was successful
            alertSwapSuccess(swap.getSourceHelper(), swap.getNotificationSuccessParameters(false));
        }
        else
        {
            // single swap was not successful
            alertSwapFailure(swap.getSourceHelper(), swap.getNotificationFailureParameters());
        }
    }

    private void processComplexResult()
    {
        if (swap.getSwapState().equals(SwapState.COMPLETED))
        {
            // complex swap was successful --> 'leading' helper
            alertSwapSuccess(swap.getSourceHelper(), swap.getNotificationSuccessParameters(false));
            // complex swap was successful --> 'following' helper (turn parameters around)
            alertSwapSuccess(swap.getTargetHelper(), swap.getNotificationSuccessParameters(true));
        }
        else
        {
            // complex swap was not successful --> 'leading' helper
            alertSwapFailure(swap.getSourceHelper(), swap.getNotificationFailureParameters());
            // complex swap was not successful --> 'following' helper (turn parameters around)
            alertSwapFailure(swap.getTargetHelper(), swap.getNotificationFailureParameters());
        }
    }

    private void alertSwapSuccess(Helper helper, Object[] parameters)
    {
        // TODO perhaps we should not use 'AbstractMailTemplate.speedysSincerely' here..
        
        HtmlGenerator generator = prepareHtmlGenerator(helper);
        AppConfiguration configuration = AppConfiguration.getInstance();
        generator.withParagraph(AppConfiguration.getInstance().getText(this, SWAP_EXECUTED, parameters))
                .withLink(configuration.getConfigurationValue(AppConfigurationValues.HELPER_CONFIRM_INFO), "zu den Helfer-Informationen")
                .withParagraph(configuration.getText(AbstractMailTemplate.class, "speedysSincerely"));
        sendMessage(helper.getEmail(), getSubject(), generator.render());
    }

    private void alertSwapFailure(Helper helper, Object[] parameters)
    {
        // TODO perhaps we should not use 'AbstractMailTemplate.speedysSincerely' here..
        
        HtmlGenerator generator = prepareHtmlGenerator(helper);
        AppConfiguration configuration = AppConfiguration.getInstance();
        generator.withParagraph(configuration.getText(this, SWAP_ABORTED, parameters)).withParagraph(
                configuration.getText(AbstractMailTemplate.class, "speedysSincerely"));
        sendMessage(helper.getEmail(), getSubject(), generator.render());
    }

    private HtmlGenerator prepareHtmlGenerator(Helper helper)
    {
        HtmlGenerator htmlGenerator = new HtmlGenerator(true).withParagraph("Hallo, " + helper.getFirstName() + "!!");
        if (swapBySystem)
        {
            htmlGenerator.withParagraph("Der Administrator hat einen Positions-Tausch vorgenommen.");
        }
        else
        {
            htmlGenerator.withParagraph("Du hast kürzlich eine Anfrage bezüglich eines Positions-Tausch bekommen.");
        }
        return htmlGenerator;
    }

    private String getSubject()
    {
        return "Ergebnis Positions-Tausch";
    }

    private void sendMessage(String recipient, String subject, String body)
    {
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de", recipient, subject, body,
                MessagingType.SWAP_RESULT, true, null);
    }
}