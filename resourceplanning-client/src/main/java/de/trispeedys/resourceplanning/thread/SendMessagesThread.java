package de.trispeedys.resourceplanning.thread;

import de.trispeedys.resourceplanning.singleton.AppSingleton;

public class SendMessagesThread extends Thread
{
    public void run()
    {
        AppSingleton.getInstance().getPort().sendAllMessages();
    }
}