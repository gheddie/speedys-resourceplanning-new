package de.trispeedys.resourceplanning.gui;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * http://stackoverflow.com/questions/574594/how-can-i-create-an-executable-jar-with-dependencies-using-maven
 * 
 * @author Stefan.Schulz
 *
 */
public class AppStarter extends JFrame implements SpeedyView
{
    private static final long serialVersionUID = 6808396399202990435L;
    
    private JDesktopPane deskPane;
    
    public AppStarter()
    {
        setTitle("Speedy Event-Admin");
        setSize(1200, 900);
        deskPane = new JDesktopPane();
        deskPane.setBackground(Color.LIGHT_GRAY);
        publishFrame(new ResourceDialog("Ressourcen", true, 
                true, true, true, this));
    }

    public void publishFrame(SpeedyView frame)
    {
        Container container = (Container) frame;
        deskPane.add(container);
        container.setLocation(0, 0);
        // container.setMaximum(true);
        container.show();
        add(deskPane);
        setVisible(true);
    }
    
    // ---

    public static void main(String[] args)
    {
        Runnable gui = new Runnable()
        {
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                new AppStarter().setVisible(true);
            }
        };
        SwingUtilities.invokeLater(gui);
    }
}