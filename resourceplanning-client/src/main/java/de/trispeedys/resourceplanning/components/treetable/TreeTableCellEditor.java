package de.trispeedys.resourceplanning.components.treetable;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableCellEditor;

public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
    private static final long serialVersionUID = -1486651810013816291L;

    private JTree tree;

    private JTable table;

    public TreeTableCellEditor(JTree tree, JTable table)
    {
        this.tree = tree;
        this.table = table;
        ((BasicTreeUI) tree.getUI()).setExpandedIcon(new ImageIcon(this.getClass()
                .getClassLoader()
                .getResource("img/expanded16px.png")));
        ((BasicTreeUI) tree.getUI()).setCollapsedIcon(new ImageIcon(this.getClass()
                .getClassLoader()
                .getResource("img/collapsed16px.png")));
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                TreeTableDataNode pathComponent =
                        (TreeTableDataNode) (((JTree) e.getSource()).getLastSelectedPathComponent());
                System.out.println(" +++ " +
                        pathComponent + " [" + pathComponent.getClass().getSimpleName() + "::" +
                        pathComponent.getEventItemType() + "::" + pathComponent.getEntityId() + "] +++ ");
                ((TreeTable) TreeTableCellEditor.this.table).setPathComponent(pathComponent);
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
    {
        return tree;
    }

    public boolean isCellEditable(EventObject e)
    {
        if (e instanceof MouseEvent)
        {
            int colunm1 = 0;
            MouseEvent me = (MouseEvent) e;
            int doubleClick = 2;
            MouseEvent newME =
                    new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() -
                            table.getCellRect(0, colunm1, true).x, me.getY(), doubleClick,
                            me.isPopupTrigger());
            tree.dispatchEvent(newME);
        }
        return false;
    }

    public Object getCellEditorValue()
    {
        return null;
    }
}