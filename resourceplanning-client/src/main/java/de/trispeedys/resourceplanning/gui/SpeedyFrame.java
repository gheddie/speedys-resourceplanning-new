package de.trispeedys.resourceplanning.gui;

import javax.swing.JInternalFrame;

public class SpeedyFrame extends JInternalFrame implements SpeedyView
{
    private SpeedyView parentFrame;

    private static final long serialVersionUID = 3993134198344742812L;

    public SpeedyFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, SpeedyView aParentFrame)
    {
        super(title, resizable, closable, maximizable);
        this.parentFrame = aParentFrame;
    }

    public void publishFrame(SpeedyView frame)
    {
         parentFrame.publishFrame(frame);
    }

    protected SpeedyView getParentFrame()
    {
        return this.parentFrame;
    }
}