package de.trispeedys.resourceplanning.listener;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;

public class GenericEntityListener implements SaveOrUpdateEventListener
{
    private static final long serialVersionUID = -8900539178362518532L;

    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException
    {
        // ...
    }
}