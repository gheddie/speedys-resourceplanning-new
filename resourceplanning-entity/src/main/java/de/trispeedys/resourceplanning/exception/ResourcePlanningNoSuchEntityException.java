package de.trispeedys.resourceplanning.exception;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;

public class ResourcePlanningNoSuchEntityException extends ResourcePlanningPersistenceException
{
    private static final long serialVersionUID = -4165700393261554620L;
    
    private static final String NO_SUCH_ENTITY = "NO_SUCH_ENTITY";

    public ResourcePlanningNoSuchEntityException(Class<? extends AbstractDbObject> entityClass, Long primaryKeyValue)
    {
        super(AppConfiguration.getInstance().getText(ResourcePlanningNoSuchEntityException.class, NO_SUCH_ENTITY, entityClass.getSimpleName(), primaryKeyValue));
    }
}