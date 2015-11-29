package de.trispeedys.resourceplanning.interaction;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class HtmlRenderer
{
    /**
     * renders success message for {@link HelperCallback} via {@link BpmMessages.RequestHelpHelper#MSG_HELP_CALLBACK}.
     * 
     * @param helperId
     * @param callback
     * @return
     */
    public static String renderCallbackSuccess(Long helperId, HelperCallback callback)
    {
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph("Danke, wir haben deine Nachricht erhalten (" + callback.getSummary() + ").")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
                .render();
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
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph("Das war ein Fehler. Die Eingabe konnte nicht verarbeitet werden. Hast du vielleicht schon mal aus diesen Link geklickt?")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
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
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak(2)
                .withParagraph("Deine Nachricht ist angekommen.")
                .withLinebreak()
                .withParagraph(
                        "Leider ist die von dir gewählte Position (" +
                                chosenPosition.getDescription() + ") bereits besetzt. " + "Du wirst in Kürze eine Mail mit Alternativvorschlägen erhalten.")
                .withLinebreak(2)
                .withParagraph("Deine Tri-Speedys.")
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
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak(2)
                .withParagraph("Deine Nachricht ist angekommen.")
                .withLinebreak()
                .withParagraph(
                        "Die von dir gewählte Position (" +
                                chosenPosition.getDescription() + ") ist verfügbar und wurde Dir zugewiesen. " +
                                "Du wirst hierzu in Kürze eine Bestätigungs-Mail hierzu erhalten.")
                .withLinebreak(2)
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public static String renderCancellationCallback(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak(2)
                .withParagraph("Deine Nachricht ist angekommen.")
                .withLinebreak()
                .withParagraph("Du wirst hierzu in Kürze eine Bestätigungs-Mail hierzu erhalten.")
                .withLinebreak(2)
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public static String renderDeactivationRecoveryCallback(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak(2)
                .withParagraph("Deine Nachricht ist angekommen.")
                .withLinebreak()
                .withParagraph("Du wirst bei der Helferplanung für die nächsten Events weiterhin berücksichtigt.")
                .withLinebreak(2)
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }
}