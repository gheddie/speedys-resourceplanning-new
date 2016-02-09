package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public abstract class RequestHelpMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    private Position position;

    public RequestHelpMailTemplate()
    {
        super(null);
    }

    public RequestHelpMailTemplate(Helper helper, Event event, Position position)
    {
        super(event);
        this.helper = helper;
        this.position = position;
    }
    
    public Helper getHelper()
    {
        return helper;
    }

    public void setHelper(Helper helper)
    {
        this.helper = helper;
    }

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }

    public void send(boolean toAdmin)
    {
        String receiverAddress = (toAdmin
                ? AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL)
                : getHelper().getEmail());
        // perhaps the receiver is a helper without a mail address set...
        if ((receiverAddress == null) || (receiverAddress.length() == 0))
        {
            return;
        }
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                receiverAddress, constructSubject(), constructBody(), getMessagingType(), true, (toAdmin
                        ? null
                        : helper));
    }
}