package de.trispeedys.resourceplanning.execution;

public class BpmJobDefinitions
{
    public class RequestHelpHelper
    {
        public static final String JOB_DEF_HELPER_REMINDER_TIMER = "HelperReminderTimer";
        
        public static final String JOB_DEF_LAST_CHANCE_TIMER = "LastChanceTimer";
        
        // timer which reminds the helper to choose a position (fires after 1 day)
        public static final String JOB_DEF_CHOOSE_POS_REMINDER_TIMER = "ChoosePosReminderTimer";
    }
}