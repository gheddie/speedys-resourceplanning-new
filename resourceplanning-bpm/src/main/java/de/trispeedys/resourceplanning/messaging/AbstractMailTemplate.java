package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public abstract class AbstractMailTemplate<T>
{
    private Helper helper;

    private Event event;

    private Position position;

    public AbstractMailTemplate()
    {
        super();
    }

    public AbstractMailTemplate(Helper helper, Event event, Position position)
    {
        super();
        this.helper = helper;
        this.event = event;
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

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }

    public abstract String constructBody();

    public abstract String constructSubject();

    public abstract MessagingType getMessagingType();

    protected String helperGreeting()
    {
        return AppConfiguration.getInstance().getText(AbstractMailTemplate.class, "helperGreeting",
                getHelper().getFirstName());
    }

    public static String sincerely()
    {
        return AppConfiguration.getInstance().getText(AbstractMailTemplate.class, "speedysSincerely");
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

    protected String getBaseLink()
    {
        return AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.HOST) +
                "/resourceplanning-bpm-" +
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.VERSION);
    }
}