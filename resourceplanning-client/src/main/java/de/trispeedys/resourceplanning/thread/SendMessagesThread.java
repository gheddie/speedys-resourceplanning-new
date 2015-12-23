package de.trispeedys.resourceplanning.thread;

import javax.swing.JOptionPane;

import de.trispeedys.resourceplanning.gui.ResourceDialog;
import de.trispeedys.resourceplanning.singleton.AppSingleton;

public class SendMessagesThread extends Thread
{
    private ResourceDialog parent;

    public SendMessagesThread(ResourceDialog parent)
    {
        super();
        this.parent = parent;
    }
    
    public void run()
    {
        try
        {
            AppSingleton.getInstance().getPort().sendAllMessages();    
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog(parent, exc.getMessage());
        }
    }
}