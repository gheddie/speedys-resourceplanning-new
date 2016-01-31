package de.trispeedys.resourceplanning.interaction;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.ManualAssignmentComment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.ManualAssignmentCommentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class JspRenderer
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
                new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                        .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                        .withParagraph(configuration.getText(JspRenderer.class, "thanks", callback.getSummary()));
        if (callback.equals(HelperCallback.ASSIGNMENT_AS_BEFORE))
        {
            if (RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helper, event) != null)
            {
                // yes, we have...
                generator = generator.withParagraph(configuration.getText(JspRenderer.class, "assignmentSucces"));
            }
            else
            {
                // no, we have not...
                generator = generator.withParagraph(configuration.getText(JspRenderer.class, "assignmentFault"));
            }
        }
        else if (callback.equals(HelperCallback.CHANGE_POS))
        {
            generator = generator.withParagraph(configuration.getText(JspRenderer.class, "notice"));
        }
        return generator.withParagraph(AbstractMailTemplate.sincerely()).render();
    }

    public static String renderCorrelationFault(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "renderCorrelationFault"))
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }
    
    public static String renderPlanningException(Long helperId, String errorMessage)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "planningExceptionCaught", errorMessage))
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    public static String renderGenericEngineFault(Long helperId, String errorMessage)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "genericEngineFailure"))
                .withParagraph(errorMessage)
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
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "messageReceived"))
                .withParagraph(configuration.getText(JspRenderer.class, "positionUnavailable", chosenPosition.getDescription()))
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
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "messageReceived"))
                .withParagraph(configuration.getText(JspRenderer.class, "positionAvailable", chosenPosition.getDescription()))
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    public static String renderCancellationConfirm(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "messageReceived"))
                .withParagraph(configuration.getText(JspRenderer.class, "cancellationConfirmed"))
                .withParagraph(configuration.getText(JspRenderer.class, "announceConfirmation"))
                .withParagraph(AbstractMailTemplate.sincerely())
                .withClosingLink()
                .render();
    }

    public static String renderDeactivationRecoveryCallback(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "messageReceived"))
                .withParagraph(configuration.getText(JspRenderer.class, "furtherRegarding"))
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    public static String renderPositionRecoveryOnCancellation(Long eventId, Long helperId, Long chosenPositionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position chosenPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(chosenPositionId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        // decide if the recovery was succesful -> position could be already taken again...
        String confirmationKey = RepositoryProvider.getRepository(HelperRepository.class).isHelperAssignedForPosition(helper, event, chosenPosition)
                ? "positionRecoveryOnCancellationSuccess"
                : "positionRecoveryOnCancellationFault";
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, confirmationKey, chosenPosition.getDescription(), chosenPosition.getDomain().getName()))
                .withParagraph(AbstractMailTemplate.sincerely())
                .render();
    }

    public static String renderManualAssignmentForm(Long eventId, Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "manualAssignmentTeaser"))
                .withTextAreaInputForm("ManualAssignmentConfirm.jsp", 6, 120, configuration.getText(JspRenderer.class, "sendManualAssignment"), eventId, helperId)
                .withClosingLink()
                .render();
    }

    public static String renderCancelForeverForm(Long eventId, Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "cancelForeverTeaser"))
                .withSimpleButtonForm("CancelForeverConfirm.jsp", configuration.getText(JspRenderer.class, "sendCancelForever"), eventId, helperId)
                .withClosingLink()
                .render();
    }

    public static String renderPauseMeForm(Long eventId, Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        // TODO perhaps render to name of the event...do it as translation parameter
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "pauseMeTeaser"))
                .withSimpleButtonForm("PauseMeConfirm.jsp", configuration.getText(JspRenderer.class, "sendPauseMe"), eventId, helperId)
                .withClosingLink()
                .render();
    }

    public static String renderAssignmentAsBeforeForm(Long eventId, Long helperId, Long priorPositionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position priorPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(priorPositionId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        String posDesc = null;
        String domainDesc = null;
        if (priorPosition != null)
        {
            posDesc = priorPosition.getDescription();
            domainDesc = priorPosition.getDomain().getName();
        }
        else
        {
            // prior position is passed through by the helper clicking a link...not the case in test cases!!
            posDesc = "[...]";
            domainDesc = "[...]";
        }
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "assignmentAsBeforeTeaser", posDesc, domainDesc))
                .withSimpleButtonForm("AssignmentAsBeforeConfirm.jsp", configuration.getText(JspRenderer.class, "sendAssignmentAsBefore"), eventId, helperId, priorPositionId)
                .withClosingLink()
                .render();
    }

    public static String renderPositionRecoveryOnCancellationForm(Long eventId, Long helperId, Long chosenPositionId)
    {
        Position chosenPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(chosenPositionId);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        String posDesc = null;
        String domainDesc = null;
        if (chosenPosition != null)
        {
            posDesc = chosenPosition.getDescription();
            domainDesc = chosenPosition.getDomain().getName();
        }
        else
        {
            // prior position is passed through by the helper clicking a link...not the case in test cases!!
            posDesc = "[...]";
            domainDesc = "[...]";
        }
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "positionRecoveryOnCancellationTeaser", posDesc, domainDesc))
                .withSimpleButtonForm("PositionRecoveryOnCancellationConfirm.jsp", configuration.getText(JspRenderer.class, "sendPositionRecoveryOnCancellation"), eventId, helperId, chosenPositionId)
                .withClosingLink()
                .render();
    }

    public static String renderChangePositionForm(Long eventId, Long helperId, Long priorPositionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        // TODO perhaps render to name of the event...do it as translation parameter
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "changePositionTeaser"))
                .withSimpleButtonForm("ChangePositionConfirm.jsp", configuration.getText(JspRenderer.class, "sendChangePosition"), eventId, helperId)
                .withClosingLink()
                .render();
    }

    public static String renderPositionChosenForm(Long eventId, Long helperId, Long chosenPositionId)
    {
        Position chosenPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(chosenPositionId);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "positionChosenTeaser", chosenPosition.getDescription(), chosenPosition.getDomain().getName()))
                .withSimpleButtonForm("PositionChosenConfirm.jsp", configuration.getText(JspRenderer.class, "sendPositionChosen"), eventId, helperId, chosenPositionId)
                .withClosingLink()
                .render();
    }

    public static String renderAssignmentCancellationForm(Long eventId, Long helperId, Long positionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findById(positionId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        String posDesc = null;
        String domainDesc = null;
        if (position != null)
        {
            posDesc = position.getDescription();
            domainDesc = position.getDomain().getName();
        }
        else
        {
            // prior position is passed through by the helper clicking a link...not the case in test cases!!
            posDesc = "[...]";
            domainDesc = "[...]";
        }
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "assignmentCancellationTeaser", posDesc, domainDesc))
                .withSimpleButtonForm("AssignmentCancellationConfirm.jsp", configuration.getText(JspRenderer.class, "sendAssignmentCancellation"), eventId, helperId)
                .withClosingLink()
                .render();
    }

    public static String renderManualAssignmentConfirmation(Long eventId, Long helperId)
    {
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        ManualAssignmentComment wish = RepositoryProvider.getRepository(ManualAssignmentCommentRepository.class).findByEventAndHelper(event, helper);
        AppConfiguration configuration = AppConfiguration.getInstance();
        HtmlGenerator generator =
                new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                        .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                        .withParagraph(configuration.getText(JspRenderer.class, "manualAssignmentConfirmed"));
        if ((wish != null) && (wish.isFilled()))
        {
            generator.withParagraph(configuration.getText(JspRenderer.class, "assignmentCommentReminder"));
            generator.withParagraph(wish.getComment());
        }
        return generator.withParagraph(AbstractMailTemplate.sincerely()).withClosingLink().render();
    }

    public static String renderCancelForeverConfirmation(Long eventId, Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "cancelForeverConfirmed"))
                .withParagraph(AbstractMailTemplate.sincerely())
                .withClosingLink()
                .render();
    }

    public static String renderPauseMeConfirmation(Long eventId, Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "pauseMeConfirmed"))
                .withParagraph(AbstractMailTemplate.sincerely())
                .withClosingLink()
                .render();
    }

    public static String renderAssignmentAsBeforeConfirmation(Long eventId, Long helperId, Long priorPositionId)
    {
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position priorPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(priorPositionId);
        String posDesc = null;
        String domainDesc = null;
        if (priorPosition != null)
        {
            posDesc = priorPosition.getDescription();
            domainDesc = priorPosition.getDomain().getName();
        }
        else
        {
            // prior position is passed through by the helper clicking a link...not the case in test cases!!
            posDesc = "[...]";
            domainDesc = "[...]";
        }
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEventAndPosition(helper, event, priorPosition) != null)
        {
            return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                    .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                    .withParagraph(configuration.getText(JspRenderer.class, "assignmentAsBeforeConfirmSuccess", posDesc, domainDesc))
                    .withParagraph(AbstractMailTemplate.sincerely())
                    .withClosingLink()
                    .render();
        }
        else
        {
            return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                    .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                    .withParagraph(configuration.getText(JspRenderer.class, "assignmentAsBeforeConfirmFault", posDesc, domainDesc))
                    .withParagraph(AbstractMailTemplate.sincerely())
                    .withClosingLink()
                    .render();
        }
    }

    public static String rendeChangePositionConfirmation(Long eventId, Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator().withImage("speedys", "gif", 600, 170)
                .withHeader(configuration.getText(JspRenderer.class, "hello", helper.getFirstName()))
                .withParagraph(configuration.getText(JspRenderer.class, "changePositionConfirmed"))
                .withParagraph(AbstractMailTemplate.sincerely())
                .withClosingLink()
                .render();
    }
}