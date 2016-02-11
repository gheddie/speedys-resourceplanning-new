package de.trispeedys.resourceplanning.entity;

public enum MessagingType
{
    // initial reminding (with repetitions)
    REMINDER_STEP_0,
    REMINDER_STEP_1,
    REMINDER_STEP_2,

    DEACTIVATION_REQUEST,
    
    // propose positions to helper, e.g. 'ASSIGNMENT_AS_BEFORE' fails
    PROPOSE_POSITIONS,
    
    // confirmation of an assignment sent to the helper
    BOOKING_CONFIRMATION,
    
    // a helper has cancelled his assignment (someone will be noticed)
    ALERT_BOOKING_CANCELLED,

    // a helper was set to inactive (someone will be noticed)
    ALERT_HELPER_DEACTIVATED,
    
    // confirm 'pause me' to helper
    PAUSE_CONFIRM,
    
    // confirm cancellation to helper
    CANCELLATION_CONFIRM,
    
    // confirm manual assignment
    MANUAL_ASSIGNMENT_CONFIRMATION,
    
    // tell helper that his process is finished
    PREPLAN_INFO,
    
    // offer a formerly blocked and then cancelled position to the initial requester
    POS_RECOVERY_ON_CANCELLATION,
    
    // confirmation on 'dont bother me no more'...
    CANCEL_FOREVER_CONFIRMATION,
    
    SWAP_RESULT,
    
    COMPLEX_SWAP_SOURCE_TRIGGER,
    
    COMPLEX_SWAP_TARGET_TRIGGER,

    // fallback
    NONE
}