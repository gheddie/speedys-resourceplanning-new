package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.service.MessagingService;

public class MailingTest
{
    //@Test
    public void testSendMails()
    {
        //clear db
        HibernateUtil.clearAll();
        
        //create message
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "Hallo", "Knallo", MessagingFormat.PLAIN).saveOrUpdate();
        
        //send
        MessagingService.sendAllUnprocessedMessages();
        
        //mail must have state 'PROCESSED'
        assertEquals(MessagingState.PROCESSED, ((MessageQueue) Datasources.getDatasource(MessageQueue.class).findAll().get(0)).getMessagingState());
    }
}