package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.rule.CallbackChoiceGenerator;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class CallbackChoiceGeneratorTest
{
    /**
     * Helper was assigned to a position in 2015, and the event 2016 is all new, so he has all the choices...
     */
    @Test
    public void testAllChoices()
    {
        HibernateUtil.clearAll();

        // create events
        Event event2015 =
                TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // get helper
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findAll().get(0);

        assertTrue(checkChoices(HelperCallback.values(),
                new CallbackChoiceGenerator().generate(helper, event2016)));
    }

    /**
     * Helper 'A' was assigned to a position in 2015, and in 2016, his position is already blocked (by helper 'B'), so
     * {@link HelperCallback#ASSIGNMENT_AS_BEFORE} must be not available...
     */
    @Test
    public void testPriorPositionAlreadyAssigned()
    {
        HibernateUtil.clearAll();

        // create events
        Event event2015 =
                TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // get helpers
        Helper helperA = (Helper) Datasources.getDatasource(Helper.class).findAll().get(0);
        Helper helperB = (Helper) Datasources.getDatasource(Helper.class).findAll().get(1);

        // assign 'B' to 'A's prior position...
        AssignmentService.assignHelper(helperB, event2016,
                AssignmentService.getPriorAssignment(helperA, event2015.getEventTemplate()).getPosition());

        assertTrue(checkChoices(new HelperCallback[]
        {
                HelperCallback.CHANGE_POS, HelperCallback.PAUSE_ME, HelperCallback.ASSIGN_ME_MANUALLY
        }, new CallbackChoiceGenerator().generate(helperA, event2016)));
    }

    /**
     * {@link Helper} 'A' chooses {@link HelperCallback#ASSIGNMENT_AS_BEFORE}, but the {@link Position} is gone in the
     * actual {@link Event} (2016)...
     */
    @Test
    public void testChoicesWithPriorPositionRemoved()
    {
        HibernateUtil.clearAll();

        // create events
        Event event2015 =
                TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI);

        // 'H3_First' is assigned to position 'pos3' with pos number '2'

        List<Integer> excludes = new ArrayList<Integer>();
        excludes.add(2);
        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, excludes, null);

        // remove prior position of helper 'A' from event 2016
        Helper helperA =
                (Helper) Datasources.getDatasource(Helper.class)
                        .findSingle(Helper.ATTR_LAST_NAME, "H3_First");
        
        assertTrue(checkChoices(new HelperCallback[]
        {
                HelperCallback.CHANGE_POS, HelperCallback.PAUSE_ME, HelperCallback.ASSIGN_ME_MANUALLY
        }, new CallbackChoiceGenerator().generate(helperA, event2016)));
    }

    // ---

    public static boolean checkChoices(HelperCallback[] expected, List<HelperCallback> generatedChoices)
    {
        if ((expected == null) && (generatedChoices == null))
        {
            return true;
        }
        else if ((expected != null) && (generatedChoices == null))
        {
            return false;
        }
        else if ((expected == null) && (generatedChoices != null))
        {
            return false;
        }
        else
        {
            if (expected.length != generatedChoices.size())
            {
                return false;
            }
            else
            {
                for (HelperCallback expectedCallback : Arrays.asList(expected))
                {
                    if (!(generatedChoices.contains(expectedCallback)))
                    {
                        return false;
                    }
                }
                return true;
            }
        }
    }
}