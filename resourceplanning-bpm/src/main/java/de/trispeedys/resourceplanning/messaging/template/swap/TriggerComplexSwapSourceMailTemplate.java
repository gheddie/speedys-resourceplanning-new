package de.trispeedys.resourceplanning.messaging.template.swap;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;

public class TriggerComplexSwapSourceMailTemplate extends TriggerComplexSwapMailTemplate
{
    public TriggerComplexSwapSourceMailTemplate(Event event, HelperAssignment sourceAssignment, HelperAssignment targetAssignment)
    {
        super(event, sourceAssignment, targetAssignment);
    }

    protected String trigger()
    {
        return TRIGGER_SOURCE;
    }

    protected Helper relevantHelper()
    {
        return getSourceAssignment().getHelper();
    }

    protected String configureBodyText()
    {
        return AppConfiguration.getInstance().getText(TriggerComplexSwapMailTemplate.class, "body", getSourceAssignment().getPosition().getDescription(),
                getSourceAssignment().getPosition().getDomain().getName(), getTargetAssignment().getPosition().getDescription(), getTargetAssignment().getPosition().getDomain().getName());
    }
}