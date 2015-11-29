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

    // fallback
    NONE
}