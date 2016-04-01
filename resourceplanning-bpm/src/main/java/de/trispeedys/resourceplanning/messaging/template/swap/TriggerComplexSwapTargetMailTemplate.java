package de.trispeedys.resourceplanning.messaging.template.swap;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.messaging.SwapMailTemplate;

public class TriggerComplexSwapTargetMailTemplate extends TriggerComplexSwapMailTemplate
{
    public TriggerComplexSwapTargetMailTemplate(GuidedEvent event, HelperAssignment sourceAssignment, HelperAssignment targetAssignment)
    {
        super(event, sourceAssignment, targetAssignment);
    }
    
    protected String trigger()
    {
        return TRIGGER_TARGET;
    }
    
    protected Helper relevantHelper()
    {
        return getTargetAssignment().getHelper();
    }
    
    protected String configureBodyText()
    {
        return AppConfiguration.getInstance().getText(SwapMailTemplate.class, "body", getTargetAssignment().getPosition().getDescription(),
                getTargetAssignment().getPosition().getDomain().getName(), getSourceAssignment().getPosition().getDescription(), getSourceAssignment().getPosition().getDomain().getName());
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.COMPLEX_SWAP_TARGET_TRIGGER;
    }
}