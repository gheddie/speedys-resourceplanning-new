package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.gravitex.hibernateadapter.core.annotation.BeforePersist;
import de.gravitex.hibernateadapter.core.annotation.EntitySaveListener;
import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;

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
    
    // @BeforePersist
    public boolean beforePersist()
    {
        return checkUniqueConfirmed();
    }
    
    // @BeforeUpdate
    public boolean beforeUpdate()
    {
        return checkUniqueConfirmed();
    }

    private boolean checkUniqueConfirmed()
    {
        if (!(isConfirmed()))
        {
            // dont care
            return true;
        }
        /**
         * only ONE assignment in state 'PLANNED' or 'CONFIRMED'
         * at a time is allowed for the same event and the same position
         */
        for (HelperAssignment helperAssignment : RepositoryProvider.getRepository(HelperAssignmentRepository.class).findAssignmentsByEventAndPosition(getEvent(), getPosition()))
        {
            if (helperAssignment.isConfirmed())
            {
                // there already is a confirmed assignment, so...
                return false;
            }
        }
        return true;
    }
}