package de.trispeedys.resourceplanning.components.treetable;

import java.awt.Dimension;

import javax.swing.JTable;

public class TreeTable extends JTable
{
    private static final long serialVersionUID = -833644592373232107L;
    
    private TreeTableCellRenderer tree;

    private TreeTableDataNode pathComponent;
    
    public TreeTable()
    {
        this(null);        
    }

    public TreeTable(AbstractTreeTableModel treeTableModel)
    {
        super();
        
        if (treeTableModel != null)
        {
            setModel(treeTableModel);
        }
    }

    public void setModel(AbstractTreeTableModel treeTableModel)
    {
        // JTree erstellen.
        tree = new TreeTableCellRenderer(this, treeTableModel);

        // Modell setzen.
        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));   
        
        // Gleichzeitiges Selektieren fuer Tree und Table.
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        tree.setSelectionModel(selectionModel); // For the tree
        
        setSelectionModel(selectionModel.getListSelectionModel());
        
        // Renderer fuer den Tree.
        setDefaultRenderer(MyTreeTableModel.class, tree);
        // Editor fuer die TreeTable
        setDefaultEditor(MyTreeTableModel.class, new TreeTableCellEditor(tree, this));

        // Kein Grid anzeigen.
        setShowGrid(false);

        // Keine Abstaende.
        setIntercellSpacing(new Dimension(0, 0));
    }
    
    public TreeTableDataNode getPathComponent()
    {
        return pathComponent;
    }

    public void setPathComponent(TreeTableDataNode pathComponent)
    {
        this.pathComponent = pathComponent;
    }
    
    @Override
    public boolean isCellEditable(int row, int column)
    {
        // TODO Blätter unbeartbeitbat machen --> nicht andere --> sonst kein Ausklappen möglich
        return super.isCellEditable(row, column);
    }
}