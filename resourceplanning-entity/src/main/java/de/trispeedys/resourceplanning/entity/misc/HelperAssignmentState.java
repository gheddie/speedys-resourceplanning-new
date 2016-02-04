package de.trispeedys.resourceplanning.entity.misc;

public enum HelperAssignmentState
{
    // result of request process --> rebooking still possible
    PLANNED,
    
    // final state - state after doing a take over to final planning
    CONFIRMED,
    
    // cancelled by helper
    CANCELLED
}