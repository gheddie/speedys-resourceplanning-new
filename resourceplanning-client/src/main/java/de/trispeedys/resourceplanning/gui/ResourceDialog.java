/*
 * Created by JFormDesigner on Sun Oct 25 22:35:29 CET 2015
 */

package de.trispeedys.resourceplanning.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.coderazzi.filters.gui.TableFilterHeader;
import de.trispeedys.resourceplanning.ResourcePlanningClientRoutines;
import de.trispeedys.resourceplanning.components.ResourcePlanningTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataModel;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataNode;
import de.trispeedys.resourceplanning.gui.builder.TableModelBuilder;
import de.trispeedys.resourceplanning.singleton.AppSingleton;
import de.trispeedys.resourceplanning.thread.SendMessagesThread;
import de.trispeedys.resourceplanning.thread.StartExecutionsThread;
import de.trispeedys.resourceplanning.webservice.EventDTO;
import de.trispeedys.resourceplanning.webservice.ExecutionDTO;
import de.trispeedys.resourceplanning.webservice.HelperDTO;
import de.trispeedys.resourceplanning.webservice.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.webservice.MessageDTO;
import de.trispeedys.resourceplanning.webservice.PositionDTO;

/**
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/ResourceInfoWs?wsdl
 * 
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/TestDataProviderWs?wsdl
 * 
 * @author Stefan.Schulz
 *
 *         http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/HelperCallbackReceiver.jsp?callbackResult=
 *         ASSIGNMENT_AS_BEFORE
 * 
 *         DEBUG Tomcat:
 * 
 *         + start by catalina.bat jpda start
 * 
 *         + debug conf + proj --> res....-bpm + socket attach + localhost:8000 + no termination
 */
public class ResourceDialog extends SpeedyFrame
{
    private static final long serialVersionUID = 2338002273562986827L;

    private static List<EventDTO> events;

    private EventDTO selectedEvent;

    private List<HelperDTO> helpers;

    private ManualAssignmentDTO selectedManualAssignment;

    private List<ManualAssignmentDTO> manualAssignments;

    private List<PositionDTO> availablePositions;

    private List<ExecutionDTO> executions;
    
    private List<MessageDTO> unsentMessages;

    private PositionDTO selectedAvailablePosition;

    // private ResourceInfo resourceInfo = null;

    private MessageDTO selectedMessage;   

    public ResourceDialog(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, SpeedyView parentFrame)
    {
        super(title, resizable, closable, maximizable, iconifiable, parentFrame);
        initComponents();
        // resourceInfo = new ResourceInfoService().getResourceInfoPort();
        setSize(800, 600);
        putListeners();
        new TableFilterHeader(tbEvents);
        new TableFilterHeader(tbHelpers);
        new TableFilterHeader(tbManualAssignments);
        new TableFilterHeader(tbAvailablePositions);
        new TableFilterHeader(tbExecutions);
        new TableFilterHeader(tbMessages);
        pgMain.setMinimum(0);
        pgMain.setMaximum(100);
        fillAll();
    }

    private void putListeners()
    {
        // events
        tbEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbEvents.getSelectedRow();
                if (selectedRow >= 0)
                {
                    int convertedRowIndex = tbEvents.convertRowIndexToModel(selectedRow);
                    eventSelected(events.get(convertedRowIndex));
                }
            }
        });
        // manual assignments
        tbManualAssignments.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbManualAssignments.getSelectedRow();
                if (selectedRow >= 0)
                {
                    int convertedRowIndex = tbManualAssignments.convertRowIndexToModel(selectedRow);
                    manualAssignmentSelected(manualAssignments.get(convertedRowIndex));
                }
            }
        });
        // available positions
        tbAvailablePositions.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbAvailablePositions.getSelectedRow();
                if (selectedRow >= 0)
                {
                    int convertedRowIndex = tbAvailablePositions.convertRowIndexToModel(selectedRow);
                    availablePositionSelected(availablePositions.get(convertedRowIndex));
                }
            }
        });
        // helpers
        tbHelpers.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbHelpers.getSelectedRow();
                if (selectedRow >= 0)
                {
                    int convertedRowIndex = tbHelpers.convertRowIndexToModel(selectedRow);
                    helperSelected(helpers.get(convertedRowIndex));
                }
            }
        });
        // messages
        tbMessages.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbMessages.getSelectedRow();
                if (selectedRow >= 0)
                {
                    int convertedRowIndex = tbMessages.convertRowIndexToModel(selectedRow);
                    mesageSelected(unsentMessages.get(convertedRowIndex));
                }
            }
        });        
    }

    private void btnFinishProcessesPressed(ActionEvent e)
    {
        if (JOptionPane.showConfirmDialog(ResourceDialog.this, "Planungen abschliessen?", "Best�tigung", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            AppSingleton.getInstance().getPort().finishUp();
        }
    }

    private void fillAll()
    {
        refreshEvents();
        refreshHelpers();
        refreshManualAssignments();
        refreshExecutions();
        refreshMessages();
    }

    private void refreshMessages()
    {
        unsentMessages = AppSingleton.getInstance().getPort().queryUnsentMessages().getItem();
        tbMessages.setModel(TableModelBuilder.createGenericTableModel(unsentMessages));        
    }

    private void refreshExecutions()
    {
        executions = AppSingleton.getInstance().getPort().queryExecutions().getItem();
        tbExecutions.setModel(TableModelBuilder.createGenericTableModel(executions));
    }

    private void refreshManualAssignments()
    {
        manualAssignments = AppSingleton.getInstance().getPort().queryManualAssignments().getItem();
        tbManualAssignments.setModel(TableModelBuilder.createGenericTableModel(manualAssignments));
    }

    private void refreshHelpers()
    {
        helpers = AppSingleton.getInstance().getPort().queryHelpers().getItem();
        tbHelpers.setModel(TableModelBuilder.createGenericTableModel(helpers));
    }

    private void refreshEvents()
    {
        events = AppSingleton.getInstance().getPort().queryEvents().getItem();
        tbEvents.setModel(TableModelBuilder.createGenericTableModel(events));
    }

    private void eventSelected(EventDTO event)
    {
        selectedEvent = event;
        System.out.println("event selected : " + event.getDescription());
        fillTree(selectedEvent.getEventId());
    }

    private void manualAssignmentSelected(ManualAssignmentDTO manualAssignment)
    {
        System.out.println("assignment selected : " + manualAssignment.getHelperName());
        selectedManualAssignment = manualAssignment;
    }
    
    private void mesageSelected(MessageDTO message)
    {
        System.out.println("message selected : " + message.getSubject());
        selectedMessage = message;        
        fillMessageDetails();
    }

    private void availablePositionSelected(PositionDTO positionDTO)
    {
        System.out.println("pos selected : " + positionDTO.getDescription());
        selectedAvailablePosition = positionDTO;
    }

    private void helperSelected(HelperDTO helperDTO)
    {
        System.out.println("helper selected : " + helperDTO.getLastName());
    }
    
    private void fillMessageDetails()
    {
        tfRecipient.setText(selectedMessage.getRecipient());
        tfSubject.setText(selectedMessage.getSubject());
        taBody.setText(selectedMessage.getBody());
    }

    private void fillTree(Long eventId)
    {
        if (eventId != null)
        {
            treeTablePositions.setModel(new TreeTableDataModel(ResourcePlanningClientRoutines.createDataStructure(eventId, chkUnassignedOnly.isSelected())));
        }
        else
        {
            // show all events
            TreeTableDataNode root = new TreeTableDataNode("root", "root", "root", "root", "root", null, null, null);
            for (EventDTO dto : events)
            {
                root.addChild(ResourcePlanningClientRoutines.createDataStructure(dto.getEventId(), false));
            }
            treeTablePositions.setModel(new TreeTableDataModel(root));
        }
        // TODO expand tree
        // treeTablePositions.expandAll();
    }

    private void tbEventsPropertyChange(PropertyChangeEvent e)
    {
        // TODO add your code here
    }

    private void btnBookManuallyPressed(ActionEvent e)
    {
        if (selectedManualAssignment == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte einen Helfer w�hlen!!");
            return;
        }
        if (selectedAvailablePosition == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte eine Position w�hlen!!");
            return;
        }
        String message =
                "Dem Helfer " + selectedManualAssignment.getHelperName() + " die freie Position " + selectedAvailablePosition.getDescription() + " zuweisen?";
        if (JOptionPane.showConfirmDialog(ResourceDialog.this, message, "Best�tigung", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            try
            {
                AppSingleton.getInstance().getPort().completeManualAssignment(selectedManualAssignment.getTaskId(), selectedAvailablePosition.getPositionId());
            }
            catch (Exception e2)
            {
                JOptionPane.showMessageDialog(ResourceDialog.this, e2.getMessage());
            }
        }
        refreshManualAssignments();
    }

    private void tdbMainStateChanged(ChangeEvent e)
    {
        System.out.println(((JTabbedPane) e.getSource()).getSelectedIndex());
    }

    private void btnReloadAvailablePositionsPressed(ActionEvent e)
    {
        if (selectedEvent == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event w�hlen!!");
            return;
        }
        // TODO ignore canceled asignments !!
        availablePositions = AppSingleton.getInstance().getPort().queryAvailablePositions(selectedEvent.getEventId()).getItem();
        tbAvailablePositions.setModel(TableModelBuilder.createGenericTableModel(availablePositions));
    }

    private void btnRefreshEventsPressed(ActionEvent e)
    {
        refreshEvents();
    }

    private void btnRefreshHelpersPressed(ActionEvent e)
    {
        refreshHelpers();
    }

    private void btnRefreshManualAssignmentsPressed(ActionEvent e)
    {
        refreshManualAssignments();
    }

    private void btnRefreshTreePressed(ActionEvent e)
    {
        if (selectedEvent == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event w�hlen!!");
            return;
        }
        fillTree(selectedEvent.getEventId());
    }

    private void btnCreateHelperPressed(ActionEvent e)
    {
        publishFrame(new HelperEditor("Dokument 1", true, true, true, true, this));
    }

    private void btnRefreshExecutionsPressed(ActionEvent e)
    {
        refreshExecutions();
    }

    private void btnSendMessagesPressed(ActionEvent e) {
        //start sending messages in a thread
        new SendMessagesThread().start();
    }
    
    private void btnPlanPressed(ActionEvent e)
    {
        //start process instances in a thread
        if (selectedEvent != null)
        {
            new StartExecutionsThread(selectedEvent.getEventId(), this).start();
        }
    }

    private void btnRefreshMessagesPressed(ActionEvent e) {
        refreshMessages();
    }

    private void btnAnonymizePressed(ActionEvent e) {
        // AppSingleton.getInstance().getPort().anonymizeHelperAddresses();
    }

    private void btnMessageFormatStateChanged(ChangeEvent e) {
        boolean selected = ((JToggleButton) e.getSource()).isSelected();
        System.out.println(selected);
        if (selected)
        {
            taBody.setContentType("text/html");
        }
        else
        {
            taBody.setContentType("text/plain");
        }
        // taBody.repaint();
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Stefan Schulz
        tbMain = new JToolBar();
        btnFinishProcesses = new JButton();
        tdbMain = new JTabbedPane();
        pnlPositions = new JPanel();
        borderPositions = new JPanel();
        scPositions = new JScrollPane();
        treeTablePositions = new TreeTable();
        btnRefreshTree = new JButton();
        chkUnassignedOnly = new JCheckBox();
        borderEvents = new JPanel();
        btnPlanEvent = new JButton();
        btnRefreshEvents = new JButton();
        scEvents = new JScrollPane();
        tbEvents = new ResourcePlanningTable();
        pnlExecutions = new JPanel();
        borderExecutions = new JPanel();
        scExecutions = new JScrollPane();
        tbExecutions = new ResourcePlanningTable();
        btnRefreshExecutions = new JButton();
        pnlHelper = new JPanel();
        borderHelpers = new JPanel();
        scHelpers = new JScrollPane();
        tbHelpers = new ResourcePlanningTable();
        btnRefreshHelpers = new JButton();
        btnCreateHelper = new JButton();
        btnAnonymize = new JButton();
        pnlManualAssignments = new JPanel();
        borderManualAssignments = new JPanel();
        scManualAssignments = new JScrollPane();
        tbManualAssignments = new ResourcePlanningTable();
        btnBookManually = new JButton();
        btnRefreshManualAssignments = new JButton();
        borderAvailablePositions = new JPanel();
        scAvailablePositions = new JScrollPane();
        tbAvailablePositions = new ResourcePlanningTable();
        btnReloadAvailablePositions = new JButton();
        pnlMessages = new JPanel();
        borderMessages = new JPanel();
        scMessages = new JScrollPane();
        tbMessages = new ResourcePlanningTable();
        btnRefreshMessages = new JButton();
        btnSendMessages = new JButton();
        borderSingleMessage = new JPanel();
        lblRecipient = new JLabel();
        tfRecipient = new JTextField();
        btnMessageFormat = new JToggleButton();
        lblSubject = new JLabel();
        tfSubject = new JTextField();
        lblBody = new JLabel();
        scBody = new JScrollPane();
        taBody = new JTextPane();
        pgMain = new JProgressBar();

        //======== this ========
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 49, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0, 0.0, 1.0E-4};

        //======== tbMain ========
        {
            tbMain.setFloatable(false);

            //---- btnFinishProcesses ----
            btnFinishProcesses.setText("Abschliessen");
            btnFinishProcesses.setIcon(new ImageIcon(getClass().getResource("/img/shutdown48px.png")));
            btnFinishProcesses.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    btnFinishProcessesPressed(e);
                }
            });
            tbMain.add(btnFinishProcesses);
        }
        contentPane.add(tbMain, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //======== tdbMain ========
        {
            tdbMain.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    tdbMainStateChanged(e);
                }
            });

            //======== pnlPositions ========
            {

                // JFormDesigner evaluation mark
                pnlPositions.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), pnlPositions.getBorder())); pnlPositions.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                pnlPositions.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlPositions.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlPositions.getLayout()).rowHeights = new int[] {203, 0, 0, 131, 0};
                ((GridBagLayout)pnlPositions.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlPositions.getLayout()).rowWeights = new double[] {1.0, 0.0, 0.0, 0.0, 1.0E-4};

                //======== borderPositions ========
                {
                    borderPositions.setBorder(new TitledBorder("Zuweisungen"));
                    borderPositions.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderPositions.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderPositions.getLayout()).rowHeights = new int[] {0, 198, 0};
                    ((GridBagLayout)borderPositions.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderPositions.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                    //======== scPositions ========
                    {
                        scPositions.setViewportView(treeTablePositions);
                    }
                    borderPositions.add(scPositions, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnRefreshTree ----
                    btnRefreshTree.setText("Aktualisieren");
                    btnRefreshTree.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshTree.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshTreePressed(e);
                        }
                    });
                    borderPositions.add(btnRefreshTree, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlPositions.add(borderPositions, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- chkUnassignedOnly ----
                chkUnassignedOnly.setText("Nur unzugewiesene");
                pnlPositions.add(chkUnassignedOnly, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== borderEvents ========
                {
                    borderEvents.setBorder(new TitledBorder("Event-Historie"));
                    borderEvents.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderEvents.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderEvents.getLayout()).rowHeights = new int[] {0, 0, 131, 0};
                    ((GridBagLayout)borderEvents.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderEvents.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                    //---- btnPlanEvent ----
                    btnPlanEvent.setText("Planen");
                    btnPlanEvent.setIcon(new ImageIcon(getClass().getResource("/img/process16px.png")));
                    btnPlanEvent.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnPlanPressed(e);
                        }
                    });
                    borderEvents.add(btnPlanEvent, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- btnRefreshEvents ----
                    btnRefreshEvents.setText("Aktualisieren");
                    btnRefreshEvents.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshEvents.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshEventsPressed(e);
                        }
                    });
                    borderEvents.add(btnRefreshEvents, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== scEvents ========
                    {

                        //---- tbEvents ----
                        tbEvents.addPropertyChangeListener(new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent e) {
                                tbEventsPropertyChange(e);
                                tbEventsPropertyChange(e);
                            }
                        });
                        scEvents.setViewportView(tbEvents);
                    }
                    borderEvents.add(scEvents, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                pnlPositions.add(borderEvents, new GridBagConstraints(0, 2, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Positionen", pnlPositions);

            //======== pnlExecutions ========
            {
                pnlExecutions.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlExecutions.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlExecutions.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)pnlExecutions.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlExecutions.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== borderExecutions ========
                {
                    borderExecutions.setBorder(new TitledBorder("Laufende Prozesse"));
                    borderExecutions.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderExecutions.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderExecutions.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)borderExecutions.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderExecutions.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                    //======== scExecutions ========
                    {
                        scExecutions.setViewportView(tbExecutions);
                    }
                    borderExecutions.add(scExecutions, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnRefreshExecutions ----
                    btnRefreshExecutions.setText("Aktualisieren");
                    btnRefreshExecutions.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshExecutions.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshExecutionsPressed(e);
                        }
                    });
                    borderExecutions.add(btnRefreshExecutions, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlExecutions.add(borderExecutions, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Prozesse", pnlExecutions);

            //======== pnlHelper ========
            {
                pnlHelper.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlHelper.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlHelper.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)pnlHelper.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlHelper.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== borderHelpers ========
                {
                    borderHelpers.setBorder(new TitledBorder("Alle Helfer"));
                    borderHelpers.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderHelpers.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderHelpers.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)borderHelpers.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderHelpers.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};

                    //======== scHelpers ========
                    {
                        scHelpers.setViewportView(tbHelpers);
                    }
                    borderHelpers.add(scHelpers, new GridBagConstraints(0, 0, 1, 4, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnRefreshHelpers ----
                    btnRefreshHelpers.setText("Aktualsieren");
                    btnRefreshHelpers.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshHelpers.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshHelpersPressed(e);
                        }
                    });
                    borderHelpers.add(btnRefreshHelpers, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- btnCreateHelper ----
                    btnCreateHelper.setText("Neu");
                    btnCreateHelper.setIcon(new ImageIcon(getClass().getResource("/img/new16px.png")));
                    btnCreateHelper.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnCreateHelperPressed(e);
                        }
                    });
                    borderHelpers.add(btnCreateHelper, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- btnAnonymize ----
                    btnAnonymize.setText("Anonymisieren");
                    btnAnonymize.setIcon(new ImageIcon(getClass().getResource("/img/anonymous16px.png")));
                    btnAnonymize.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnAnonymizePressed(e);
                        }
                    });
                    borderHelpers.add(btnAnonymize, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlHelper.add(borderHelpers, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Helfer", pnlHelper);

            //======== pnlManualAssignments ========
            {
                pnlManualAssignments.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlManualAssignments.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)pnlManualAssignments.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                //======== borderManualAssignments ========
                {
                    borderManualAssignments.setBorder(new TitledBorder("Unzugewiesene Helfer"));
                    borderManualAssignments.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderManualAssignments.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderManualAssignments.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)borderManualAssignments.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderManualAssignments.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                    //======== scManualAssignments ========
                    {
                        scManualAssignments.setViewportView(tbManualAssignments);
                    }
                    borderManualAssignments.add(scManualAssignments, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnBookManually ----
                    btnBookManually.setText("Buchen");
                    btnBookManually.setIcon(new ImageIcon(getClass().getResource("/img/process16px.png")));
                    btnBookManually.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnBookManuallyPressed(e);
                        }
                    });
                    borderManualAssignments.add(btnBookManually, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- btnRefreshManualAssignments ----
                    btnRefreshManualAssignments.setText("Aktualisieren");
                    btnRefreshManualAssignments.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshManualAssignments.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshManualAssignmentsPressed(e);
                        }
                    });
                    borderManualAssignments.add(btnRefreshManualAssignments, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlManualAssignments.add(borderManualAssignments, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== borderAvailablePositions ========
                {
                    borderAvailablePositions.setBorder(new TitledBorder("Verf\u00fcgbare Positionen"));
                    borderAvailablePositions.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderAvailablePositions.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderAvailablePositions.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)borderAvailablePositions.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderAvailablePositions.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                    //======== scAvailablePositions ========
                    {
                        scAvailablePositions.setViewportView(tbAvailablePositions);
                    }
                    borderAvailablePositions.add(scAvailablePositions, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnReloadAvailablePositions ----
                    btnReloadAvailablePositions.setText("Aktualisieren");
                    btnReloadAvailablePositions.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnReloadAvailablePositions.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnReloadAvailablePositionsPressed(e);
                        }
                    });
                    borderAvailablePositions.add(btnReloadAvailablePositions, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlManualAssignments.add(borderAvailablePositions, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Manuelle Zuweisungen", pnlManualAssignments);

            //======== pnlMessages ========
            {
                pnlMessages.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlMessages.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlMessages.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)pnlMessages.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlMessages.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                //======== borderMessages ========
                {
                    borderMessages.setBorder(new TitledBorder("Unversandte Nachrichten"));
                    borderMessages.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderMessages.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderMessages.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)borderMessages.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderMessages.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                    //======== scMessages ========
                    {
                        scMessages.setViewportView(tbMessages);
                    }
                    borderMessages.add(scMessages, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnRefreshMessages ----
                    btnRefreshMessages.setText("Aktualisieren");
                    btnRefreshMessages.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshMessages.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshMessagesPressed(e);
                        }
                    });
                    borderMessages.add(btnRefreshMessages, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- btnSendMessages ----
                    btnSendMessages.setText("Alle senden");
                    btnSendMessages.setIcon(new ImageIcon(getClass().getResource("/img/mail16px.png")));
                    btnSendMessages.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            btnSendMessagesPressed(e);
                        }
                    });
                    borderMessages.add(btnSendMessages, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlMessages.add(borderMessages, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== borderSingleMessage ========
                {
                    borderSingleMessage.setBorder(new TitledBorder("Gew\u00e4hlte Nachricht"));
                    borderSingleMessage.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderSingleMessage.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)borderSingleMessage.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)borderSingleMessage.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderSingleMessage.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                    //---- lblRecipient ----
                    lblRecipient.setText("Empf\u00e4nger:");
                    borderSingleMessage.add(lblRecipient, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- tfRecipient ----
                    tfRecipient.setEditable(false);
                    borderSingleMessage.add(tfRecipient, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- btnMessageFormat ----
                    btnMessageFormat.setText("HTML/Plain");
                    btnMessageFormat.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            btnMessageFormatStateChanged(e);
                        }
                    });
                    borderSingleMessage.add(btnMessageFormat, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- lblSubject ----
                    lblSubject.setText("Betreff:");
                    borderSingleMessage.add(lblSubject, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- tfSubject ----
                    tfSubject.setEditable(false);
                    borderSingleMessage.add(tfSubject, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- lblBody ----
                    lblBody.setText("Text:");
                    borderSingleMessage.add(lblBody, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //======== scBody ========
                    {

                        //---- taBody ----
                        taBody.setEditable(false);
                        taBody.setContentType("text/html");
                        scBody.setViewportView(taBody);
                    }
                    borderSingleMessage.add(scBody, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                pnlMessages.add(borderSingleMessage, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Nachrichten", pnlMessages);
        }
        contentPane.add(tdbMain, new GridBagConstraints(0, 1, 3, 2, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));
        contentPane.add(pgMain, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Stefan Schulz
    private JToolBar tbMain;
    private JButton btnFinishProcesses;
    private JTabbedPane tdbMain;
    private JPanel pnlPositions;
    private JPanel borderPositions;
    private JScrollPane scPositions;
    private TreeTable treeTablePositions;
    private JButton btnRefreshTree;
    private JCheckBox chkUnassignedOnly;
    private JPanel borderEvents;
    private JButton btnPlanEvent;
    private JButton btnRefreshEvents;
    private JScrollPane scEvents;
    private ResourcePlanningTable tbEvents;
    private JPanel pnlExecutions;
    private JPanel borderExecutions;
    private JScrollPane scExecutions;
    private ResourcePlanningTable tbExecutions;
    private JButton btnRefreshExecutions;
    private JPanel pnlHelper;
    private JPanel borderHelpers;
    private JScrollPane scHelpers;
    private ResourcePlanningTable tbHelpers;
    private JButton btnRefreshHelpers;
    private JButton btnCreateHelper;
    private JButton btnAnonymize;
    private JPanel pnlManualAssignments;
    private JPanel borderManualAssignments;
    private JScrollPane scManualAssignments;
    private ResourcePlanningTable tbManualAssignments;
    private JButton btnBookManually;
    private JButton btnRefreshManualAssignments;
    private JPanel borderAvailablePositions;
    private JScrollPane scAvailablePositions;
    private ResourcePlanningTable tbAvailablePositions;
    private JButton btnReloadAvailablePositions;
    private JPanel pnlMessages;
    private JPanel borderMessages;
    private JScrollPane scMessages;
    private ResourcePlanningTable tbMessages;
    private JButton btnRefreshMessages;
    private JButton btnSendMessages;
    private JPanel borderSingleMessage;
    private JLabel lblRecipient;
    private JTextField tfRecipient;
    private JToggleButton btnMessageFormat;
    private JLabel lblSubject;
    private JTextField tfSubject;
    private JLabel lblBody;
    private JScrollPane scBody;
    private JTextPane taBody;
    private JProgressBar pgMain;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
