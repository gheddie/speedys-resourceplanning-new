package de.trispeedys.resourceplanning.entity.util;

import java.util.Calendar;
import java.util.Date;

import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.DomainResponsibility;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.HelperHistory;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;
import de.trispeedys.resourceplanning.entity.builder.AggregationRelationBuilder;
import de.trispeedys.resourceplanning.entity.builder.DomainBuilder;
import de.trispeedys.resourceplanning.entity.builder.DomainResponsibilityBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventPositionBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventTemplateBuilder;
import de.trispeedys.resourceplanning.entity.builder.HelperAssignmentBuilder;
import de.trispeedys.resourceplanning.entity.builder.HelperBuilder;
import de.trispeedys.resourceplanning.entity.builder.HelperHistoryBuilder;
import de.trispeedys.resourceplanning.entity.builder.MessageQueueBuilder;
import de.trispeedys.resourceplanning.entity.builder.PositionAggregationBuilder;
import de.trispeedys.resourceplanning.entity.builder.PositionBuilder;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.HistoryType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class EntityFactory
{
    public static Helper buildHelper(String lastName, String firstName, String email, HelperState helperState, Date dateOfBirth)
    {
        Helper result =
                new HelperBuilder().withFirstName(firstName)
                        .withLastName(lastName)
                        .withDateOfBirth(dateOfBirth)
                        .withEmail(email)
                        .withHelperState(helperState)
                        .build();
        result.setCode(SpeedyRoutines.createHelperCode(result));
        return result;
    }

    public static Helper buildHelper(String lastName, String firstName, String email, HelperState helperState, int dayOfBirth, int monthOfBirth, int yearOfBirth)
    {
        Calendar dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(Calendar.DAY_OF_MONTH, dayOfBirth);
        dateOfBirth.set(Calendar.MONTH, monthOfBirth - 1);
        dateOfBirth.set(Calendar.YEAR, yearOfBirth);
        Helper result =
                new HelperBuilder().withFirstName(firstName)
                        .withLastName(lastName)
                        .withDateOfBirth(dateOfBirth.getTime())
                        .withEmail(email)
                        .withHelperState(helperState)
                        .build();
        result.setCode(SpeedyRoutines.createHelperCode(result));
        return result;
    }

    public static HelperHistory buildHelperHistory(Helper helper, Event event, HistoryType historyType)
    {
        HelperHistory result = new HelperHistoryBuilder().withHelper(helper).withEvent(event).withHistoryType(historyType).build();
        return result;
    }

    public static HelperAssignment buildHelperAssignment(Helper helper, Event event, Position position)
    {
        return buildHelperAssignment(helper, event, position, HelperAssignmentState.PLANNED);
    }

    public static HelperAssignment buildHelperAssignment(Helper helper, Event event, Position position, HelperAssignmentState helperAssignmentState)
    {
        if (!(PositionService.isPositionPresentInEvent(position, event)))
        {
            throw new ResourcePlanningException("helper '" +
                    helper + "' can not be commited to position '" + position + "' as it is not present in event '" + event + "'.");
        }
        return new HelperAssignmentBuilder().withHelper(helper)
                .withPosition(position)
                .withEvent(event)
                .withHelperAssignmentState(helperAssignmentState)
                .build();
    }

    public static Position buildPosition(String description, int minimalAge, Domain domain, int positionNumber, boolean choosable)
    {
        return buildPosition(description, minimalAge, domain, positionNumber, choosable, null);
    }

    public static Position buildPosition(String description, int minimalAge, Domain domain, int positionNumber, boolean choosable, Integer assignmentPriority)
    {
        return new PositionBuilder().withDescription(description)
                .withMinimalAge(minimalAge)
                .withDomain(domain)
                .withPositionNumber(positionNumber)
                .withChoosable(choosable)
                .withAssignmentPriority(assignmentPriority)
                .build();
    }

    public static EventTemplate buildEventTemplate(String description)
    {
        return new EventTemplateBuilder().withDescription(description).build();
    }

    public static Event buildEvent(String description, String eventKey, int day, int month, int year, EventState eventState, EventTemplate eventTemplate,
            Event parentEvent)
    {
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(Calendar.DAY_OF_MONTH, day);
        eventDate.set(Calendar.MONTH, month - 1);
        eventDate.set(Calendar.YEAR, year);
        return new EventBuilder().withDescription(description)
                .withDate(eventDate.getTime())
                .withEventKey(eventKey)
                .withEventState(eventState)
                .withEventTemplate(eventTemplate)
                .withParentEvent(parentEvent)
                .build();
    }

    public static MessageQueue buildMessageQueue(String fromAddress, String toAddress, String subject, String body, MessagingFormat messagingFormat)
    {
        return buildMessageQueue(fromAddress, toAddress, subject, body, MessagingType.NONE, messagingFormat);
    }

    public static MessageQueue buildMessageQueue(String fromAddress, String toAddress, String subject, String body, MessagingType messagingType,
            MessagingFormat messagingFormat)
    {
        return new MessageQueueBuilder().withFromAddress(fromAddress)
                .withToAddress(toAddress)
                .withSubject(subject)
                .withBody(body)
                .withMessagingType(messagingType)
                .withMessagingFormat(messagingFormat)
                .build();
    }

    public static EventPosition buildEventPosition(Event event, Position position)
    {
        return new EventPositionBuilder().withEvent(event).withPosition(position).build();
    }

    public static DomainResponsibility buildDomainResponsibility(Domain domain, Helper helper)
    {
        return new DomainResponsibilityBuilder().withDomain(domain).withHelper(helper).build();
    }

    public static Domain buildDomain(String name, int domainNumber)
    {
        return new DomainBuilder().withDomainNumber(domainNumber).withName(name).build();
    }

    public static PositionAggregation buildPositionAggregation(String name)
    {
        return new PositionAggregationBuilder().withName(name).build();
    }

    public static AggregationRelation buildAggregationRelation(Position position, PositionAggregation positionAggregation)
    {
        return new AggregationRelationBuilder().withPosition(position).withPositionAggregation(positionAggregation).build();
    }
}