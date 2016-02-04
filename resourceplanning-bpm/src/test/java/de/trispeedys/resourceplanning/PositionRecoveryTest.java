package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.MissedAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.interaction.HelperConfirmation;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.MissedAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;

public class PositionRecoveryTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    // TODO see inner implementation !!
    // @Test
    // TODO reactivate test if recovery is completely durchdacht
    @Deployment(resources = "RequestHelp.bpmn")
    public void testMissedAssignmentAndRecovery()
    {
        // clear db
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        // start all the process
        String businessKey = null;
        List<Helper> activeHelpers = RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();
        List<ProcessInstance> executions = new ArrayList<ProcessInstance>();
        for (Helper helper : activeHelpers)
        {
            businessKey = BusinessKeys.generateRequestHelpBusinessKey(helper, event2016);
            executions.add(RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine));
        }

        // pick two helpers
        Helper helper1 = RepositoryProvider.getRepository(HelperRepository.class).findAll(null).get(0);
        Helper helper2 = RepositoryProvider.getRepository(HelperRepository.class).findAll(null).get(1);

        // helper '2' wants another position and chooses prior position of '1'...
        HelperConfirmation.processChangePositionConfirmation(event2016.getId(), helper2.getId(), processEngine.getProcessEngine());
        Position priorPositionHelper1 = RepositoryProvider.getRepository(HelperAssignmentRepository.class).getPriorAssignment(helper1, event2016.getEventTemplate()).getPosition();
        HelperConfirmation.processPositionChosenConfirmation(event2016.getId(), helper2.getId(), priorPositionHelper1.getId(), processEngine.getProcessEngine());

        // helper '1' wants his prior positions, but does not get it (as it is blocked)...
        HelperConfirmation.processAssignmentAsBeforeConfirmation(event2016.getId(), helper1.getId(), null, processEngine.getProcessEngine());

        // there must be missed assignment
        assertEquals(1, RepositoryProvider.getRepository(MissedAssignmentRepository.class).findAll(null).size());

        // helper '2' cancels his assignment...
        HelperConfirmation.processAssignmentCancellationConfirm(event2016.getId(), helper2.getId(), null, processEngine.getProcessEngine());

        // old helper must have gotten the recovery mail...
        List<MessageQueue> messages = RepositoryProvider.getRepository(MessageQueueRepository.class).findByHelperAndMessagingType(helper1, MessagingType.POS_RECOVERY_ON_CANCELLATION);

        // there must be one recovery mail...
        assertEquals(1, messages.size());

        // ...to helper 1 !!
        assertTrue(messages.get(0).getHelper().getId().equals(helper1.getId()));

        // now that the helper ('1') has got the mail, he can use it to claim to proposed and recovered position via
        // 'HelperInteraction'...
        HelperConfirmation.processPositionRecoveryConfirmation(event2016.getId(), helper1.getId(), priorPositionHelper1.getId(), processEngine.getProcessEngine());

        // finally, helper '1' should be assigned to his prior position...
        HelperAssignment assignment = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helper1, event2016);

        assertTrue(assignment != null);
        assertEquals(HelperAssignmentState.PLANNED, assignment.getHelperAssignmentState());
    }

    /**
     * - (1) helper 1 misses a position, so a missed assignment is created for him. - (2) position is cancelled ->
     * helper 1 gets opportunity to retake it (A) and does so. - (3) helper 1 cancels it again.
     * 
     * Now helper 1 must NOT get another recovery mail saying him he can retake the position he just cancelled. This
     * would be the case is the missed assignment would NOT be set to 'used' after ('A').
     */
    // @Test
    // TODO reactivate test if recovery is completely durchdacht
    @Deployment(resources = "RequestHelp.bpmn")
    public void testMissedAssignmentOutdated()
    {
        // clear db
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        // start all the process
        String businessKey = null;
        List<Helper> activeHelpers = RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();
        List<ProcessInstance> executions = new ArrayList<ProcessInstance>();
        for (Helper helper : activeHelpers)
        {
            businessKey = BusinessKeys.generateRequestHelpBusinessKey(helper, event2016);
            executions.add(RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine));
        }

        // pick two helpers
        Helper helper1 = RepositoryProvider.getRepository(HelperRepository.class).findAll(null).get(0);
        Helper helper2 = RepositoryProvider.getRepository(HelperRepository.class).findAll(null).get(1);

        // helper '2' wants another position and chooses prior position of '1'...
        HelperConfirmation.processChangePositionConfirmation(event2016.getId(), helper2.getId(), processEngine.getProcessEngine());
        Position priorPositionHelper1 = RepositoryProvider.getRepository(HelperAssignmentRepository.class).getPriorAssignment(helper1, event2016.getEventTemplate()).getPosition();
        HelperConfirmation.processPositionChosenConfirmation(event2016.getId(), helper2.getId(), priorPositionHelper1.getId(), processEngine.getProcessEngine());

        // helper '1' wants his prior positions, but does not get it (as it is blocked)...
        HelperConfirmation.processAssignmentAsBeforeConfirmation(event2016.getId(), helper1.getId(), null, processEngine.getProcessEngine());

        // there must be missed assignment
        assertEquals(1, RepositoryProvider.getRepository(MissedAssignmentRepository.class).findAll(null).size());

        // helper '2' cancels his assignment...
        HelperConfirmation.processAssignmentCancellationConfirm(event2016.getId(), helper2.getId(), null, processEngine.getProcessEngine());

        // old helper must have gotten the recovery mail...
        List<MessageQueue> messages = RepositoryProvider.getRepository(MessageQueueRepository.class).findByHelperAndMessagingType(helper1, MessagingType.POS_RECOVERY_ON_CANCELLATION);

        // there must be one recovery mail...
        assertEquals(1, messages.size());

        // ...to helper 1 !!
        assertTrue(messages.get(0).getHelper().getId().equals(helper1.getId()));

        // additionally, the sould be a subscription to message 'MSG_ASSIG_RECOVERY'...
        assertTrue(BpmHelper.checkMessageSubscription(event2016.getId(), helper1.getId(), BpmMessages.RequestHelpHelper.MSG_ASSIG_RECOVERY, processEngine.getProcessEngine()));

        // now that the helper ('1') has got the mail, he can use it to claim to proposed and recovered position via
        // 'HelperInteraction'...
        HelperConfirmation.processPositionRecoveryConfirmation(event2016.getId(), helper1.getId(), priorPositionHelper1.getId(), processEngine.getProcessEngine());

        List<MissedAssignment> missedAssignments = RepositoryProvider.getRepository(MissedAssignmentRepository.class).findAll();

        // there must be only one missed assignment which must be set to 'used'...
        assertEquals(1, missedAssignments.size());
        assertTrue(missedAssignments.get(0).isUsed());

        // TODO implement test (to the end) --> do cancellation for helper 1 and make sure there is no second recovery
        // mail
    }
}