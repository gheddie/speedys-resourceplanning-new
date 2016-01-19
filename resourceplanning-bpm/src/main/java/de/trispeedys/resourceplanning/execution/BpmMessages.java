package de.trispeedys.resourceplanning.execution;

public class BpmMessages
{
    public class RequestHelpHelper
    {
        // Startet einen Slave-Prozess
        public static final String MSG_HELP_TRIG = "MSG_HELP_TRIG";
        
        // Meldet den Abschluss eines Slave-Prozesses zurück an den Master
        public static final String MSG_HELP_FINISHED = "MSG_HELP_FINISHED";        
        
        public static final String MSG_START_REMINDERS = "MSG_START_REMINDERS";
        
        public static final String MSG_HELP_CALLBACK = "MSG_HELP_CALLBACK";
        
        public static final String MSG_DEACT_RESP = "MSG_DEACT_RESP";

        // helper has chosen a position
        public static final String MSG_POS_CHOSEN = "MSG_POS_CHOSEN";
        
        // assigment cancelled by helper
        public static final String MSG_ASSIG_CANCELLED = "MSG_ASSIG_CANCELLED";
        
        // assigment cancelled -> told to prior requester
        // -> prior requester claims he wants to be assigned as the assignment is available again
        public static final String MSG_ASSIG_RECOVERY = "MSG_ASSIG_RECOVERY";
    }
}