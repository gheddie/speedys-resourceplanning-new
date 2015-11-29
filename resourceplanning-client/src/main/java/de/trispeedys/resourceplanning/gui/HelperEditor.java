/*
 * Created by JFormDesigner on Fri Nov 20 15:09:52 CET 2015
 */

package de.trispeedys.resourceplanning.gui;

import java.awt.*;

import javax.swing.*;

/**
 * @author Stefan Schulz
 */
public class HelperEditor extends SpeedyFrame
{
    private static final long serialVersionUID = -7633207300171439594L;

    public HelperEditor(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, SpeedyView parentFrame)
    {
        super(title, resizable, closable, maximizable, iconifiable, parentFrame);
        setSize(800, 600);
        initComponents();
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Stefan Schulz
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        textField2 = new JTextField();
        label3 = new JLabel();
        textField3 = new JTextField();
        label4 = new JLabel();
        textField4 = new JTextField();

        // ======== this ========
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[]
        {
                0, 0, 0
        };
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[]
        {
                0, 0, 0, 0, 0
        };
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[]
        {
                0.0, 1.0, 1.0E-4
        };
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[]
        {
                0.0, 0.0, 0.0, 0.0, 1.0E-4
        };

        // ---- label1 ----
        label1.setText("Nachname:");
        contentPane.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(textField1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
                0));

        // ---- label2 ----
        label2.setText("Vorname:");
        contentPane.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(textField2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
                0));

        // ---- label3 ----
        label3.setText("Geburtsdatum:");
        contentPane.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(textField3, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
                0));

        // ---- label4 ----
        label4.setText("E-Mail");
        contentPane.add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        contentPane.add(textField4, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
                0));
        // JFormDesigner - End of component initialization //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Stefan Schulz
    private JLabel label1;

    private JTextField textField1;

    private JLabel label2;

    private JTextField textField2;

    private JLabel label3;

    private JTextField textField3;

    private JLabel label4;

    private JTextField textField4;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
