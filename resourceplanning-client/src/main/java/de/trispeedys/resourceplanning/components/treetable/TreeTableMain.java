package de.trispeedys.resourceplanning.components.treetable;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TreeTableMain extends JFrame
{
    public TreeTableMain()
    {
        super("Tree Table Demo");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(0, 1));

        AbstractTreeTableModel treeTableModel = null;

        TreeTable myTreeTable = new TreeTable(treeTableModel);

        Container cPane = getContentPane();

        cPane.add(new JScrollPane(myTreeTable));

        setSize(1000, 800);
        setLocationRelativeTo(null);

    }

    public static void main(final String[] args)
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
                new TreeTableMain().setVisible(true);
            }
        };
        SwingUtilities.invokeLater(gui);
    }
}