package de.trispeedys.resourceplanning.execution;

public class BpmVariables
{
    public class Misc
    {
        public static final String VAR_EVENT_ID = "eventId";
    }
    
    public class RequestHelpHelper
    {
        public static final String VAR_SUPERVISION_REQUIRED = "supervisionRequired";
        
        public static final String VAR_HELPER_ID = "helperId";

        public static final String VAR_HELPER_CALLBACK = "helperCallback";       

        public static final String VAR_PRIOR_POS_AVAILABLE = "priorPosAvailable";
        
        public static final String VAR_CHOSEN_POS_AVAILABLE = "chosenPosAvailable";
        
        public static final String VAR_MAIL_ATTEMPTS = "mailAttempts";
        
        public static final String VAR_CHOSEN_POSITION = "chosenPosition";

        // The position which the helper was assigned to the last time
        public static final String VAR_PRIOR_POSITION = "priorPosition";
        
        // Sind dem Helfer momentan Positionen vorschlagbar (ja/nein)
        public static final String VAR_POSITIONS_PROPOSABLE = "positionsProposable";

        public static final String VAR_POS_CHOOSING_REENTRANT = "posChoosingReentrant";

        public static final String VAR_HELPER_MANUAL_ASSIGNMENT_WISH = "manualAssignmentWish";
        
        public static final String VAR_DEACTIVATION_ON_TIMEOUT = "deactivationOnTimeout";

        public static final String VAR_HELPER_CODE = "helperCode";
    }
    
    public class RequestHelpMaster
    {
        /**
         * business of the master process given to the slave in order to call back finishing.
         * slave must not start with out this given on startup... 
         */
        public static final String VAR_MASTER_BK_REF = "masterBkRef";
        
        // Event-ID, user by master process
        public static final String VAR_MASTER_EVENT_ID = "masterEventId";
        
        public static final String VAR_ACTIVE_HELPER_IDS = "activeHelperIds";
        
        public static final String VAR_ACTIVE_HELPER_ID = "activeHelperId";
    }
    
    public class Swap
    {
        /**
         * If true: Desired swap is automatically done by the system (if possible). If false:
         * swap is done by user interaction.
         */
        public static final String VAR_SWAP_BY_SYSTEM = "swapBySystem";
        
        // answer of source helper in a 'not to null' swap
        public static final String VAR_NOT_TO_NULL_SWAP_SOURCE_OK = "notToNullSwapSourceOk";
        
        // answer of target helper in a 'not to null' swap
        public static final String VAR_NOT_TO_NULL_SWAP_TARGET_OK = "notToNullSwapTargetOk";
        
        // answer of (the only) helper in a 'to null' swap
        public static final String VAR_TO_NULL_SWAP_OK = "toNullSwapOk";
        
        // type of swap (assigned to null [if true] - switch between two assigned positions [if false])
        public static final String VAR_IS_TO_NULL_SWAP = "isToNullSwap";

        public static final String VAR_POS_ID_SOURCE = "posIdSource";
        
        public static final String VAR_POS_ID_TARGET = "posIdTarget";
        
        public static final String VAR_HELPER_ID_SOURCE = "helperIdSource";
        
        public static final String VAR_HELPER_ID_TARGET = "helperIdTarget";

        // the reference for the underlying assignment swap entity
        public static final String VAR_SWAP_ENTITY_ID = "swapEntityId";
    }
}