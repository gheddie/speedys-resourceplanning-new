package de.trispeedys.resourceplanning.thread;

import javax.swing.JOptionPane;

import de.trispeedys.resourceplanning.gui.ResourceDialog;
import de.trispeedys.resourceplanning.singleton.AppSingleton;

public class StartExecutionsThread extends Thread
{
    private Long eventId;

    private ResourceDialog parent;

    public StartExecutionsThread(Long eventId, ResourceDialog parent)
    {
        super();
        this.eventId = eventId;
        this.parent = parent;
    }

    public void run()
    {
        try
        {
            AppSingleton.getInstance().getPort().startProcessesForActiveHelpersByEventId(eventId);
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog(parent, exc.getMessage());
        }
    }
}