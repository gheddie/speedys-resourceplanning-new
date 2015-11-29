package de.trispeedys.resourceplanning.entity.misc;

public enum HelperAssignmentState
{
    // result of request process --> rebooking still possible
    PLANNED,
    
    // final state - not (yet) in scope of the process
    CONFIRMED,
    
    // cancelled by helper
    CANCELLED
}