package de.trispeedys.resourceplanning.components.treetable;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;

public class TreeTableSelectionModel extends DefaultTreeSelectionModel
{
    private static final long serialVersionUID = -530155318091269044L;

    public TreeTableSelectionModel()
    {
        super();

        getListSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {

            }
        });
    }

    ListSelectionModel getListSelectionModel()
    {
        return listSelectionModel;
    }
}