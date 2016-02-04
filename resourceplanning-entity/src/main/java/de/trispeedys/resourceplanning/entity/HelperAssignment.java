package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;

@Entity
@Table(name = "helper_assignment")
public class HelperAssignment extends AbstractAssignment
{
    public static final String ATTR_ASSIGNMENT_STATE = "helperAssignmentState";    
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "helper_assignment_state")
    private HelperAssignmentState helperAssignmentState;
    
    public HelperAssignmentState getHelperAssignmentState()
    {
        return helperAssignmentState;
    }
    
    public void setHelperAssignmentState(HelperAssignmentState helperAssignmentState)
    {
        this.helperAssignmentState = helperAssignmentState;
    }

    public boolean isCancelled()
    {
        return (helperAssignmentState.equals(helperAssignmentState.CANCELLED));
    }

    public boolean isConfirmed()
    {
        return (helperAssignmentState.equals(helperAssignmentState.CONFIRMED));
    }
}