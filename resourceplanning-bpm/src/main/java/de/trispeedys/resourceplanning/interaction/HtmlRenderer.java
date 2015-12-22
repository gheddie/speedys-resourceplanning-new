package de.trispeedys.resourceplanning.interaction;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class HtmlRenderer
{
    /**
     * renders success message for {@link HelperCallback} via {@link BpmMessages.RequestHelpHelper#MSG_HELP_CALLBACK}.
     * 
     * @param helperId
     * @param callback
     * @return
     */
    public static String renderCallbackSuccess(Long eventId, Long helperId, HelperCallback callback)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        HtmlGenerator generator =
                new HtmlGenerator().withHeader(
                        configuration.getText(HtmlRenderer.class, "hello", helper.getFirstName())).withParagraph(
                        configuration.getText(HtmlRenderer.class, "thanks", callback.getSummary()));
        if (callback.equals(HelperCallback.ASSIGNMENT_AS_BEFORE))
        {
            // TODO we can check, if now there is an assignment (and if so, tell it to the user)
            if (RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helper, event) != null)
            {
                // yes, we have...
                generator = generator.withParagraph(configuration.getText(HtmlRenderer.class, "assignmentSucces"));
            }
            else
            {
                // no, we have not...
                generator = generator.withParagraph(configuration.getText(HtmlRenderer.class, "assignmentFault"));
            }
        }
        else if (callback.equals(HelperCallback.CHANGE_POS))
        {
            generator = generator.withParagraph(configuration.getText(HtmlRenderer.class, "notice"));
        }
        return generator.withParagraph(AbstractMailTemplate.sincerely()).render();
    }

    /**
     * RETURN_MESSAGE_UNPROCESSABLE
     * 
     * @param helperId
     * @return
     */
    public static String renderCorrelationFault(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withHeader(configuration.getText(HtmlRenderer.class, "hello", helper.getFirstName()))
                .withLinebreak()
                .withParagraph(configuration.getText(HtmlRenderer.class, "renderCorrelationFault"))
                .withLinebreak()
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    /**
     * called from chosen position receiver jsp. Informs the user that his message has been received and gives an
     * warning, that he will receive another mail if the chosen position is not available.
     * 
     * HelperInteraction.RETURN_POS_CHOSEN_POS_TAKEN
     * 
     * @param chosenPositionId
     * @return
     */
    public static String renderChosenPositionUnavailableCallback(Long helperId, Long chosenPositionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position chosenPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(chosenPositionId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withHeader(configuration.getText(HtmlRenderer.class, "hello", helper.getFirstName()))
                .withLinebreak(2)
                .withParagraph(configuration.getText(HtmlRenderer.class, "messageReceived"))
                .withLinebreak()
                // TODO translate                
                .withParagraph(
                        "Leider ist die von dir gewählte Position (" +
                                chosenPosition.getDescription() + ") bereits besetzt. " +
                                "Du wirst in Kürze eine Mail mit Alternativvorschlägen erhalten.")
                .withLinebreak(2)
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    /**
     * HelperInteraction.RETURN_POS_CHOSEN_NOMINAL
     * 
     * @param helperId
     * @param chosenPositionId
     * @return
     */
    public static String renderChosenPositionAvailableCallback(Long helperId, Long chosenPositionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position chosenPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(chosenPositionId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withHeader(configuration.getText(HtmlRenderer.class, "hello", helper.getFirstName()))
                .withLinebreak(2)
                .withParagraph(configuration.getText(HtmlRenderer.class, "messageReceived"))
                .withLinebreak()
                // TODO translate
                .withParagraph(
                        "Die von dir gewählte Position (" +
                                chosenPosition.getDescription() + ") ist verfügbar und wurde Dir zugewiesen. " +
                                "Du wirst hierzu in Kürze eine Bestätigungs-Mail hierzu erhalten.")
                .withLinebreak(2)
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    public static String renderCancellationCallback(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withHeader(configuration.getText(HtmlRenderer.class, "hello", helper.getFirstName()))
                .withLinebreak(2)
                .withParagraph(configuration.getText(HtmlRenderer.class, "messageReceived"))
                .withLinebreak()
                .withParagraph(configuration.getText(HtmlRenderer.class, "announceConfirmation"))
                .withLinebreak(2)
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    public static String renderDeactivationRecoveryCallback(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withHeader(configuration.getText(HtmlRenderer.class, "hello", helper.getFirstName()))
                .withLinebreak(2)
                .withParagraph(configuration.getText(HtmlRenderer.class, "messageReceived"))
                .withLinebreak()
                .withParagraph(configuration.getText(HtmlRenderer.class, "furtherRegarding"))
                .withLinebreak(2)
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }
}