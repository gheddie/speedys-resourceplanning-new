package de.trispeedys.resourceplanning.entity.misc;

public enum SwapState
{
    // swap requested by admin
    REQUESTED,
    
    // completed sucessfully 
    COMPLETED,
    
    // interrupted by admin
    INTERRUPTED,
    
    // rejected by helper (simple swap) or at least one of the helper (complex swap)
    REJECTED
}