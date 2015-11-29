package de.trispeedys.resourceplanning.execution;

public class BpmVariables
{
    public class RequestHelpHelper
    {
        public static final String VAR_SUPERVISION_REQUIRED = "supervisionRequired";
        
        // public static final String VAR_HELPER_CODE = "helperCode";
        
        public static final String VAR_HELPER_ID = "helperId";

        public static final String VAR_HELPER_CALLBACK = "helperCallback";

        public static final String VAR_EVENT_ID = "eventId";

        public static final String VAR_PRIOR_POS_AVAILABLE = "priorPosAvailable";
        
        public static final String VAR_CHOSEN_POS_AVAILABLE = "chosenPosAvailable";

        public static final String VAR_ACTIVE_HELPER_IDS = "activeHelperIds";
        
        public static final String VAR_ACTIVE_HELPER_ID = "activeHelperId";
        
        public static final String VAR_MAIL_ATTEMPTS = "mailAttempts";
        
        public static final String VAR_CHOSEN_POSITION = "chosenPosition";

        // The position which the helper was assigned to the last time
        public static final String VAR_PRIOR_POSITION = "priorPosition";
        
        // Sind dem Helfer momentan Positionen vorschlagbar (ja/nein)
        public static final String VAR_POSITIONS_PROPOSABLE = "positionsProposable";

        public static final String VAR_POS_CHOOSING_REENTRANT = "posChoosingReentrant";
    }
}