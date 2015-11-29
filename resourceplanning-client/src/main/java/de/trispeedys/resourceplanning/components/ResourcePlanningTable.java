package de.trispeedys.resourceplanning.components;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ResourcePlanningTable extends JTable
{
    private static final long serialVersionUID = -5816470393008211811L;
    
    public ResourcePlanningTable()
    {
        super();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
}