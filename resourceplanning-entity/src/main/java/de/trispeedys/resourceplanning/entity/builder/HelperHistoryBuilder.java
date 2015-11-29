package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperHistory;
import de.trispeedys.resourceplanning.entity.misc.HistoryType;

public class HelperHistoryBuilder extends AbstractEntityBuilder<HelperHistory>
{
    private Helper helper;
    
    private Event event;
    
    private HistoryType historyType;

    public HelperHistoryBuilder withHelper(Helper aHelper)
    {
        this.helper = aHelper;
        return this;
    }

    public HelperHistoryBuilder withEvent(Event aEvent)
    {
        this.event = aEvent;
        return this;
    }

    public HelperHistoryBuilder withHistoryType(HistoryType aHistoryType)
    {
        this.historyType = aHistoryType;
        return this;
    }
    
    public HelperHistory build()
    {
        HelperHistory helperHistory = new HelperHistory();
        helperHistory.setHelper(helper);
        helperHistory.setEvent(event);
        helperHistory.setHistoryType(historyType);
        helperHistory.setCreationTime(new Date());
        return helperHistory;
    }
}