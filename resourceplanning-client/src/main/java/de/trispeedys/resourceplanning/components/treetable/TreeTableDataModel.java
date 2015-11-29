package de.trispeedys.resourceplanning.components.treetable;

public class TreeTableDataModel extends AbstractTreeTableModel
{
    private static final int COL_DESCRIPTION = 0;
    
    private static final int COL_ASSIGNMENT = 1;
    
    private static final int COL_GROUP = 2;
    
    private static final int COL_PRIORIZATION = 3;

    private static final int COL_AVAILABILITY = 4;

    // Spalten Name.
    static protected String[] columnNames =
    {
            "Beschreibung", "Besetzung", "Gruppe", "Priorität", "Wählbar"
    };

    // Spalten Typen.
    static protected Class<?>[] columnTypes =
    {
            MyTreeTableModel.class, String.class, String.class, String.class, String.class, String.class
    };

    public TreeTableDataModel(TreeTableDataNode rootNode)
    {
        super(rootNode);
        root = rootNode;
    }

    public Object getChild(Object parent, int index)
    {
        return ((TreeTableDataNode) parent).getChildren().get(index);
    }

    public int getChildCount(Object parent)
    {
        return ((TreeTableDataNode) parent).getChildren().size();
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public String getColumnName(int column)
    {
        return columnNames[column];
    }

    public Class<?> getColumnClass(int column)
    {
        return columnTypes[column];
    }

    public Object getValueAt(Object node, int column)
    {
        switch (column)
        {
            case COL_DESCRIPTION:
                return ((TreeTableDataNode) node).getDescription();
            case COL_ASSIGNMENT:
                return ((TreeTableDataNode) node).getAssignment();
            case COL_GROUP:
                return ((TreeTableDataNode) node).getGroup();
            case COL_PRIORIZATION:
                return ((TreeTableDataNode) node).getPriorization();                
            case COL_AVAILABILITY:
                return ((TreeTableDataNode) node).getAvailability();                
            default:
                break;
        }
        return null;
    }

    public boolean isCellEditable(Object node, int column)
    {
        return true; // Important to activate TreeExpandListener
    }

    public void setValueAt(Object aValue, Object node, int column)
    {
    }

}