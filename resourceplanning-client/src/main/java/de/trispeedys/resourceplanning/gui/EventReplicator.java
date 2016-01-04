/*
 * Created by JFormDesigner on Sun Dec 27 15:57:11 CET 2015
 */

package de.trispeedys.resourceplanning.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import de.trispeedys.resourceplanning.singleton.AppSingleton;
import de.trispeedys.resourceplanning.webservice.EventDTO;

/**
 * @author Stefan Schulz
 */
public class EventReplicator extends SpeedyFrame
{
    private static final long serialVersionUID = -4699645932728491795L;

    private EventDTO event;

    public EventReplicator(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable,
            SpeedyView parentFrame, EventDTO event)
    {
        super(title, resizable, closable, maximizable, iconifiable, parentFrame);
        this.event = event;
        setSize(800, 600);
        initComponents();
    }

    private void btnOkPressed(ActionEvent e)
    {
        try
        {
            AppSingleton.getInstance()
                    .getPort()
                    .duplicateEvent(event.getEventId(), tfDescription.getText(), tfKey.getText(), 1, 1, 1980);
            dispose();
        }
        catch (Exception e2)
        {
            JOptionPane.showMessageDialog(EventReplicator.this, e2.getMessage());
        }
    }

    private void btnAbortPressed(ActionEvent e)
    {
        dispose();
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        lblDescription = new JLabel();
        tfDescription = new JTextField();
        lblKey = new JLabel();
        tfKey = new JTextField();
        lblDate = new JLabel();
        tfDate = new JTextField();
        btnOk = new JButton();
        btnAbort = new JButton();

        // ======== this ========
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[]
        {
                0, 0, 0, 0
        };
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[]
        {
                0, 0, 0, 0, 0, 0
        };
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[]
        {
                0.0, 1.0, 0.0, 1.0E-4
        };
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[]
        {
                0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4
        };

        // ---- lblDescription ----
        lblDescription.setText("Beschreibung:");
        contentPane.add(lblDescription, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(tfDescription, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        // ---- lblKey ----
        lblKey.setText("Schl\u00fcssel:");
        contentPane.add(lblKey, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(tfKey, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        // ---- lblDate ----
        lblDate.setText("Datum:");
        contentPane.add(lblDate, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(tfDate, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        // ---- btnOk ----
        btnOk.setText("OK");
        btnOk.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                btnOkPressed(e);
            }
        });
        contentPane.add(btnOk, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        // ---- btnAbort ----
        btnAbort.setText("Abbrechen");
        btnAbort.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                btnAbortPressed(e);
            }
        });
        contentPane.add(btnAbort, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel lblDescription;

    private JTextField tfDescription;

    private JLabel lblKey;

    private JTextField tfKey;

    private JLabel lblDate;

    private JTextField tfDate;

    private JButton btnOk;

    private JButton btnAbort;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
