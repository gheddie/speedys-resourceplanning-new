/*
 * Created by JFormDesigner on Sun Oct 25 22:35:29 CET 2015
 */

package de.trispeedys.resourceplanning.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.itextpdf.text.DocumentException;

import net.coderazzi.filters.gui.TableFilterHeader;
import de.trispeedys.resourceplanning.ResourcePlanningClientRoutines;
import de.trispeedys.resourceplanning.components.ResourcePlanningTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataModel;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataNode;
import de.trispeedys.resourceplanning.export.EventExporter;
import de.trispeedys.resourceplanning.gui.builder.TableModelBuilder;
import de.trispeedys.resourceplanning.singleton.AppSingleton;
import de.trispeedys.resourceplanning.thread.SendMessagesThread;
import de.trispeedys.resourceplanning.thread.StartExecutionsThread;
import de.trispeedys.resourceplanning.util.HierarchicalEventItemType;
import de.trispeedys.resourceplanning.util.marshalling.ListMarshaller;
import de.trispeedys.resourceplanning.webservice.EventDTO;
import de.trispeedys.resourceplanning.webservice.ExecutionDTO;
import de.trispeedys.resourceplanning.webservice.HelperDTO;
import de.trispeedys.resourceplanning.webservice.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.webservice.MessageDTO;
import de.trispeedys.resourceplanning.webservice.PositionDTO;

/**
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-1.0-RC-3/ResourceInfoWs?wsdl
 * wsimport -keep -verbose http://www.triathlon-helfer.de:8080/resourceplanning-bpm-1.0-RC-3/ResourceInfoWs?wsdl
 * 
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-1.0-RC-3/TestDataProviderWs?wsdl
 * wsimport -keep -verbose http://www.triathlon-helfer.de:8080/resourceplanning-bpm-1.0-RC-3/TestDataProviderWs?wsdl
 * 
 * @author Stefan.Schulz
 *
 *         http://localhost:8080/resourceplanning-bpm-1.0-RC-3/HelperCallbackReceiver.jsp?callbackResult=
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

    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy_HH_mm_ss");

    private static List<EventDTO> events;

    private EventDTO selectedEvent;

    private List<HelperDTO> helpers;

    private ManualAssignmentDTO selectedManualAssignment;

    private List<ManualAssignmentDTO> manualAssignments;

    private List<PositionDTO> availablePositions;

    private List<ExecutionDTO> executions;

    private List<MessageDTO> unsentMessages;

    private PositionDTO selectedAvailablePosition;

    private MessageDTO selectedMessage;

    private List<PositionDTO> positionsToAdd;

    private List<PositionDTO> selectedPositionsToAdd;

    private List<PositionDTO> positionsToRemove;

    private List<PositionDTO> selectedPositionsToRemove;

    private static final int TABINDEX_PROCESSES = 1;

    private static final int TABINDEX_HELPER = 2;

    private static final int TABINDEX_MANUAL_ASSIGNMENTS = 3;

    private static final int TABINDEX_EVENT_TUNING = 4;

    private static final int TABINDEX_MESSAGES = 5;

    private static final int TABINDEX_POST_PROCESSING = 6;

    public ResourceDialog(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable,
            SpeedyView parentFrame)
    {
        super(title, resizable, closable, maximizable, iconifiable, parentFrame);
        initComponents();
        // resourceInfo = new ResourceInfoService().getResourceInfoPort();
        setSize(800, 600);
        configureTables();
        new TableFilterHeader(tbEvents);
        new TableFilterHeader(tbHelpers);
        new TableFilterHeader(tbManualAssignments);
        new TableFilterHeader(tbAvailablePositions);
        new TableFilterHeader(tbExecutions);
        new TableFilterHeader(tbMessages);
        new TableFilterHeader(tbPosAdding);
        new TableFilterHeader(tbPosRemoving);
        pgMain.setMinimum(0);
        pgMain.setMaximum(100);
        fillAll();
        enableTabs(1, 0, 0, 0, 0, 0, 0);
        updateTitle();
    }

    private void updateTitle()
    {
        // TODO render something better
        setTitle(AppSingleton.getInstance().getPort() + "");
    }

    private void configureTables()
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
        // post processing source
        treeTablePostProcessingSource.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                System.out.println("...source...");
            }
        });
        // post processing target
        treeTablePostProcessingTarget.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                System.out.println("...target...");
            }
        });
        // adding positions
        tbPosAdding.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                List<PositionDTO> list = new ArrayList<PositionDTO>();
                for (int selectedRow : tbPosAdding.getSelectedRows())
                {
                    list.add(positionsToAdd.get(tbPosAdding.convertRowIndexToModel(selectedRow)));
                }
                posToAddSelected(list);
            }
        });
        tbPosAdding.enableMultiSelection();
        // removing positions
        tbPosRemoving.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                List<PositionDTO> list = new ArrayList<PositionDTO>();
                for (int selectedRow : tbPosRemoving.getSelectedRows())
                {
                    list.add(positionsToRemove.get(tbPosRemoving.convertRowIndexToModel(selectedRow)));
                }
                posToRemoveSelected(list);
            }
        });
        tbPosRemoving.enableMultiSelection();
    }
    
    private void btnClipboardPressed(ActionEvent e) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(taBody.getText()), null);
    }

    private void btnFinishProcessesPressed(ActionEvent e)
    {
        if (JOptionPane.showConfirmDialog(ResourceDialog.this, "Planungen abschliessen?", "Bestätigung",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            AppSingleton.getInstance().getPort().finishUp();
        }
    }

    private void fillAll()
    {
        refreshEvents();
        refreshHelpers();
        // refreshManualAssignments();
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
        if (selectedEvent == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event wählen!!");
            return;
        }
        manualAssignments = AppSingleton.getInstance().getPort().queryManualAssignments(selectedEvent.getEventId()).getItem();
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
        updateAssignmentProgress(event.getAssignmentCount(), event.getPositionCount());
        selectedEvent = event;
        System.out.println("event selected : " + event.getDescription());
        fillTree(selectedEvent.getEventId());
        fillPostProcessing(selectedEvent.getEventId());
        enableTabs(selectedEvent.getEventState());
    }

    private void posToAddSelected(List<PositionDTO> positionDTO)
    {
        selectedPositionsToAdd = positionDTO;
    }

    private void posToRemoveSelected(List<PositionDTO> positionDTO)
    {
        selectedPositionsToRemove = positionDTO;
    }

    private void enableTabs(String eventState)
    {
        switch (eventState)
        {
            case "PLANNED":
                enableTabs(1, 0, 1, 0, 1, 0, 0);
                break;
            case "INITIATED":
                enableTabs(1, 1, 1, 1, 0, 1, 1);
                break;
            case "FINISHED":
                enableTabs(1, 0, 1, 0, 0, 0, 0);
                break;
        }
    }

    private void enableTabs(int... enableStates)
    {
        enableTab(TABINDEX_PROCESSES, enableStates);
        enableTab(TABINDEX_HELPER, enableStates);
        enableTab(TABINDEX_MANUAL_ASSIGNMENTS, enableStates);
        enableTab(TABINDEX_EVENT_TUNING, enableStates);
        enableTab(TABINDEX_MESSAGES, enableStates);
        enableTab(TABINDEX_POST_PROCESSING, enableStates);
    }

    private void enableTab(int index, int... enableStates)
    {
        tdbMain.setEnabledAt(index, enableStates[index] == 1
                ? true
                : false);
    }

    private void manualAssignmentSelected(ManualAssignmentDTO manualAssignment)
    {
        System.out.println("assignment selected : " + manualAssignment.getHelperName());
        selectedManualAssignment = manualAssignment;
        updateManualAssignmentWish(manualAssignment.getWish());
    }

    private void updateManualAssignmentWish(String wish)
    {
        taWish.setText(wish);
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

    private void fillPostProcessing(Long eventId)
    {
        if (eventId != null)
        {
            TreeTableDataModel model =
                    new TreeTableDataModel(ResourcePlanningClientRoutines.createDataStructure(eventId, true));
            treeTablePostProcessingSource.setModel(model);
            treeTablePostProcessingTarget.setModel(model);
        }
    }

    private void fillTree(Long eventId)
    {
        if (eventId != null)
        {
            treeTablePositions.setModel(new TreeTableDataModel(ResourcePlanningClientRoutines.createDataStructure(
                    eventId, false)));
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
        // ...
    }

    private void btnBookManuallyPressed(ActionEvent e)
    {
        if (selectedManualAssignment == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte einen Helfer wählen!!");
            return;
        }
        if (selectedAvailablePosition == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte eine Position wählen!!");
            return;
        }
        String message =
                "Dem Helfer " +
                        selectedManualAssignment.getHelperName() + " die freie Position " +
                        selectedAvailablePosition.getDescription() + " zuweisen?";
        if (JOptionPane.showConfirmDialog(ResourceDialog.this, message, "Bestätigung", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            try
            {
                AppSingleton.getInstance()
                        .getPort()
                        .completeManualAssignment(selectedManualAssignment.getTaskId(),
                                selectedAvailablePosition.getPositionId());
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
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event wählen!!");
            return;
        }
        availablePositions =
                AppSingleton.getInstance().getPort().queryAvailablePositions(selectedEvent.getEventId()).getItem();
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
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event wählen!!");
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

    private void btnSendMessagesPressed(ActionEvent e)
    {
        // start sending messages in a thread
        new SendMessagesThread(this).start();
    }

    private void btnPlanPressed(ActionEvent e)
    {
        // start process instances in a thread
        if (selectedEvent != null)
        {
            new StartExecutionsThread(selectedEvent.getEventId(), this).start();
        }
    }

    private void btnRefreshMessagesPressed(ActionEvent e)
    {
        refreshMessages();
    }

    private void btnRefreshPostProcessingPressed(ActionEvent e)
    {
        if (selectedEvent == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event wählen!!");
            return;
        }
        fillPostProcessing(selectedEvent.getEventId());
    }

    private void btnMessageFormatStateChanged(ChangeEvent e)
    {
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

    private void btnSwapPositionsPressed(ActionEvent e)
    {
        TreeTableDataNode sourceSwapNode = treeTablePostProcessingSource.getPathComponent();
        TreeTableDataNode targetSwapNode = treeTablePostProcessingTarget.getPathComponent();

        if ((sourceSwapNode == null) || (targetSwapNode == null))
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte eine Quelle und ein Ziel wählen!!");
            return;
        }
        if ((!(sourceSwapNode.getEventItemType().equals(HierarchicalEventItemType.POSITION))) ||
                (!(targetSwapNode.getEventItemType().equals(HierarchicalEventItemType.POSITION))))
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Quelle und Ziel müssen Positionen sein!!");
            return;
        }
        if (sourceSwapNode.getEntityId().equals(targetSwapNode.getEntityId()))
        {
            JOptionPane.showMessageDialog(ResourceDialog.this,
                    "Quellen - und Ziel-Position müssen unterschiedlich sein!!");
            return;
        }
        if (JOptionPane.showConfirmDialog(ResourceDialog.this, "Positionen '" +
                sourceSwapNode.getDescription() + "' und '" + targetSwapNode.getDescription() + "' tauschen?",
                "Bestätigung", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            // TODO pass position ids instead of '-1l'...
            AppSingleton.getInstance().getPort().swapPositions(sourceSwapNode.getEntityId(), targetSwapNode.getEntityId(), selectedEvent.getEventId());
        }
    }

    private void updateAssignmentProgress(int assignmentCount, int positionCount)
    {
        pgMain.setMinimum(0);
        pgMain.setValue(assignmentCount);
        pgMain.setMaximum(positionCount);
    }

    private void btnExportEventPressed(ActionEvent e)
    {
        if (selectedEvent == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event wählen!!");
            return;
        }
        try
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(this);
            File file = chooser.getSelectedFile();
            new EventExporter(selectedEvent.getEventId(), selectedEvent.getPositionCount(),
                    selectedEvent.getAssignmentCount()).export(file.getAbsolutePath() +
                    "\\" + selectedEvent.getDescription() + "_" + df.format(new Date()) + ".pdf");
        }
        catch (FileNotFoundException | DocumentException e1)
        {
            e1.printStackTrace();
        }
    }

    private void btnRefreshAddRemovePosPressed(ActionEvent e)
    {
        refreshTuningPositions();
    }

    private void refreshTuningPositions()
    {
        if (selectedEvent == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event wählen!!");
            return;
        }
        // positions to add
        positionsToAdd =
                AppSingleton.getInstance().getPort().queryEventPositions(selectedEvent.getEventId(), false).getItem();

        tbPosAdding.setModel(TableModelBuilder.createGenericTableModel(positionsToAdd));
        // positions to remove
        positionsToRemove =
                AppSingleton.getInstance().getPort().queryEventPositions(selectedEvent.getEventId(), true).getItem();

        tbPosRemoving.setModel(TableModelBuilder.createGenericTableModel(positionsToRemove));
    }

    private void btnAddPositionPressed(ActionEvent e)
    {
        // TODO translate !!
        if ((selectedPositionsToAdd == null) || (selectedPositionsToAdd.size() == 0))
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte eine oder mehrere Position(en) wählen!!");
            return;
        }
        if (JOptionPane.showConfirmDialog(ResourceDialog.this, "Position(en) hinzufügen?", "Bestätigung",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            AppSingleton.getInstance()
                    .getPort()
                    .addPositionsToEvent(selectedEvent.getEventId(),
                            ListMarshaller.marshall(retrievePosNumbers(selectedPositionsToAdd)));
            JOptionPane.showMessageDialog(ResourceDialog.this, "Position(en) wurde(n) dem Event hinzugefügt!!");
            refreshTuningPositions();
        }
        selectedPositionsToAdd = null;
    }

    private void btnRemovePositionPressed(ActionEvent e)
    {
        // TODO translate !!
        if ((selectedPositionsToRemove == null) || (selectedPositionsToRemove.size() == 0))
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte eine oder mehrere Position(en) wählen!!");
            return;
        }
        if (JOptionPane.showConfirmDialog(ResourceDialog.this, "Position(en) löschen?", "Bestätigung",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            AppSingleton.getInstance()
                    .getPort()
                    .removePositionsFromEvent(selectedEvent.getEventId(),
                            ListMarshaller.marshall(retrievePosNumbers(selectedPositionsToRemove)));
            JOptionPane.showMessageDialog(ResourceDialog.this, "Position(en) wurde(n) aus dem Event entfernt!!");
            refreshTuningPositions();
        }
        selectedPositionsToRemove = null;
    }

    private List<Integer> retrievePosNumbers(List<PositionDTO> dtos)
    {
        List<Integer> result = new ArrayList<>();
        for (PositionDTO dto : dtos)
        {
            result.add(dto.getPositionNumber());
        }
        return result;
    }

    private void btnDuplicateEventPressed(ActionEvent e) {
        if (selectedEvent == null)
        {
            JOptionPane.showMessageDialog(ResourceDialog.this, "Bitte ein Event wählen!!");
            return;
        }
        publishFrame(new EventReplicator("Dokument 1", true, true, true, true, this, selectedEvent));
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
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
        btnExportEvent = new JButton();
        btnDuplicateEvent = new JButton();
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
        pnlManualAssignments = new JPanel();
        borderManualAssignments = new JPanel();
        scManualAssignments = new JScrollPane();
        tbManualAssignments = new ResourcePlanningTable();
        btnBookManually = new JButton();
        btnRefreshManualAssignments = new JButton();
        borderWish = new JPanel();
        scWish = new JScrollPane();
        taWish = new JTextArea();
        borderAvailablePositions = new JPanel();
        scAvailablePositions = new JScrollPane();
        tbAvailablePositions = new ResourcePlanningTable();
        btnReloadAvailablePositions = new JButton();
        pnlEventTuning = new JPanel();
        borderPosAdding = new JPanel();
        scPosAdding = new JScrollPane();
        tbPosAdding = new ResourcePlanningTable();
        btnAddPosition = new JButton();
        tbTuning = new JToolBar();
        btnRefreshAddRemovePos = new JButton();
        borderPosRemoving = new JPanel();
        scPosRemoving = new JScrollPane();
        tbPosRemoving = new ResourcePlanningTable();
        btnRemovePosition = new JButton();
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
        btnClipboard = new JButton();
        lblBody = new JLabel();
        scBody = new JScrollPane();
        taBody = new JTextPane();
        pnlPostProcessing = new JPanel();
        borderPostProcessingSource = new JPanel();
        scPostProcessingSource = new JScrollPane();
        treeTablePostProcessingSource = new TreeTable();
        tbPostProcessing = new JToolBar();
        btnRefreshPostProcessing = new JButton();
        btnSwapPositions = new JButton();
        borderPostProcessingTarget = new JPanel();
        scPostProcessingTarget = new JScrollPane();
        treeTablePostProcessingTarget = new TreeTable();
        pgMain = new JProgressBar();

        //======== this ========
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 49, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0, 0.0, 1.0E-4};

        //======== tbMain ========
        {
            tbMain.setFloatable(false);

            //---- btnFinishProcesses ----
            btnFinishProcesses.setText("Abschliessen");
            btnFinishProcesses.setIcon(new ImageIcon(getClass().getResource("/img/shutdown48px.png")));
            btnFinishProcesses.addActionListener(new ActionListener() {
                @Override
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
                @Override
                public void stateChanged(ChangeEvent e) {
                    tdbMainStateChanged(e);
                }
            });

            //======== pnlPositions ========
            {
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
                        @Override
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
                    ((GridBagLayout)borderEvents.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 131, 0};
                    ((GridBagLayout)borderEvents.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderEvents.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

                    //---- btnPlanEvent ----
                    btnPlanEvent.setText("Planen");
                    btnPlanEvent.setIcon(new ImageIcon(getClass().getResource("/img/process16px.png")));
                    btnPlanEvent.addActionListener(new ActionListener() {
                        @Override
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
                        @Override
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
                            @Override
                            public void propertyChange(PropertyChangeEvent e) {
                                tbEventsPropertyChange(e);
                                tbEventsPropertyChange(e);
                            }
                        });
                        scEvents.setViewportView(tbEvents);
                    }
                    borderEvents.add(scEvents, new GridBagConstraints(0, 0, 1, 5, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnExportEvent ----
                    btnExportEvent.setText("Exportieren");
                    btnExportEvent.setIcon(new ImageIcon(getClass().getResource("/img/export16px.png")));
                    btnExportEvent.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnExportEventPressed(e);
                        }
                    });
                    borderEvents.add(btnExportEvent, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- btnDuplicateEvent ----
                    btnDuplicateEvent.setText("Neu erstellen");
                    btnDuplicateEvent.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnDuplicateEventPressed(e);
                        }
                    });
                    borderEvents.add(btnDuplicateEvent, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
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
                        @Override
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
                    ((GridBagLayout)borderHelpers.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)borderHelpers.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderHelpers.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                    //======== scHelpers ========
                    {
                        scHelpers.setViewportView(tbHelpers);
                    }
                    borderHelpers.add(scHelpers, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnRefreshHelpers ----
                    btnRefreshHelpers.setText("Aktualisieren");
                    btnRefreshHelpers.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshHelpers.addActionListener(new ActionListener() {
                        @Override
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
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnCreateHelperPressed(e);
                        }
                    });
                    borderHelpers.add(btnCreateHelper, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
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
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowHeights = new int[] {0, 67, 0, 0};
                ((GridBagLayout)pnlManualAssignments.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};

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
                        @Override
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
                        @Override
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

                //======== borderWish ========
                {
                    borderWish.setBorder(new TitledBorder("Helferwunsch"));
                    borderWish.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderWish.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)borderWish.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)borderWish.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)borderWish.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== scWish ========
                    {

                        //---- taWish ----
                        taWish.setLineWrap(true);
                        scWish.setViewportView(taWish);
                    }
                    borderWish.add(scWish, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                pnlManualAssignments.add(borderWish, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

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
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnReloadAvailablePositionsPressed(e);
                        }
                    });
                    borderAvailablePositions.add(btnReloadAvailablePositions, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlManualAssignments.add(borderAvailablePositions, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Manuelle Zuweisungen", pnlManualAssignments);

            //======== pnlEventTuning ========
            {
                pnlEventTuning.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlEventTuning.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlEventTuning.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)pnlEventTuning.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlEventTuning.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};

                //======== borderPosAdding ========
                {
                    borderPosAdding.setBorder(new TitledBorder("Position hinzuf\u00fcgen"));
                    borderPosAdding.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderPosAdding.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderPosAdding.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)borderPosAdding.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderPosAdding.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                    //======== scPosAdding ========
                    {
                        scPosAdding.setViewportView(tbPosAdding);
                    }
                    borderPosAdding.add(scPosAdding, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnAddPosition ----
                    btnAddPosition.setText("Hinzuf\u00fcgen");
                    btnAddPosition.setIcon(new ImageIcon(getClass().getResource("/img/add16px.png")));
                    btnAddPosition.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnAddPositionPressed(e);
                        }
                    });
                    borderPosAdding.add(btnAddPosition, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlEventTuning.add(borderPosAdding, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== tbTuning ========
                {
                    tbTuning.setFloatable(false);

                    //---- btnRefreshAddRemovePos ----
                    btnRefreshAddRemovePos.setText("Aktualisieren");
                    btnRefreshAddRemovePos.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshAddRemovePos.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshAddRemovePosPressed(e);
                        }
                    });
                    tbTuning.add(btnRefreshAddRemovePos);
                }
                pnlEventTuning.add(tbTuning, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== borderPosRemoving ========
                {
                    borderPosRemoving.setBorder(new TitledBorder("Position entfernen"));
                    borderPosRemoving.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderPosRemoving.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)borderPosRemoving.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)borderPosRemoving.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)borderPosRemoving.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                    //======== scPosRemoving ========
                    {
                        scPosRemoving.setViewportView(tbPosRemoving);
                    }
                    borderPosRemoving.add(scPosRemoving, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- btnRemovePosition ----
                    btnRemovePosition.setText("Entfernen");
                    btnRemovePosition.setIcon(new ImageIcon(getClass().getResource("/img/remove16px.png")));
                    btnRemovePosition.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnRemovePositionPressed(e);
                        }
                    });
                    borderPosRemoving.add(btnRemovePosition, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                pnlEventTuning.add(borderPosRemoving, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Event-Tuning", pnlEventTuning);

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
                        @Override
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
                        @Override
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
                        @Override
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

                    //---- btnClipboard ----
                    btnClipboard.setText("Zwischenablage");
                    btnClipboard.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnClipboardPressed(e);
                        }
                    });
                    borderSingleMessage.add(btnClipboard, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

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

            //======== pnlPostProcessing ========
            {
                pnlPostProcessing.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlPostProcessing.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlPostProcessing.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)pnlPostProcessing.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlPostProcessing.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};

                //======== borderPostProcessingSource ========
                {
                    borderPostProcessingSource.setBorder(new TitledBorder("Quelle"));
                    borderPostProcessingSource.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderPostProcessingSource.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)borderPostProcessingSource.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)borderPostProcessingSource.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)borderPostProcessingSource.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== scPostProcessingSource ========
                    {
                        scPostProcessingSource.setViewportView(treeTablePostProcessingSource);
                    }
                    borderPostProcessingSource.add(scPostProcessingSource, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                pnlPostProcessing.add(borderPostProcessingSource, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== tbPostProcessing ========
                {
                    tbPostProcessing.setFloatable(false);

                    //---- btnRefreshPostProcessing ----
                    btnRefreshPostProcessing.setText("Aktualisieren");
                    btnRefreshPostProcessing.setIcon(new ImageIcon(getClass().getResource("/img/reload16px.png")));
                    btnRefreshPostProcessing.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnRefreshPostProcessingPressed(e);
                        }
                    });
                    tbPostProcessing.add(btnRefreshPostProcessing);

                    //---- btnSwapPositions ----
                    btnSwapPositions.setText("Tauschen");
                    btnSwapPositions.setIcon(new ImageIcon(getClass().getResource("/img/swap16px.png")));
                    btnSwapPositions.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnSwapPositionsPressed(e);
                        }
                    });
                    tbPostProcessing.add(btnSwapPositions);
                }
                pnlPostProcessing.add(tbPostProcessing, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== borderPostProcessingTarget ========
                {
                    borderPostProcessingTarget.setBorder(new TitledBorder("Ziel"));
                    borderPostProcessingTarget.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderPostProcessingTarget.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)borderPostProcessingTarget.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)borderPostProcessingTarget.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)borderPostProcessingTarget.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== scPostProcessingTarget ========
                    {
                        scPostProcessingTarget.setViewportView(treeTablePostProcessingTarget);
                    }
                    borderPostProcessingTarget.add(scPostProcessingTarget, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                pnlPostProcessing.add(borderPostProcessingTarget, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Nachbearbeitung", pnlPostProcessing);
        }
        contentPane.add(tdbMain, new GridBagConstraints(0, 1, 3, 3, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- pgMain ----
        pgMain.setStringPainted(true);
        contentPane.add(pgMain, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
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
    private JButton btnExportEvent;
    private JButton btnDuplicateEvent;
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
    private JPanel pnlManualAssignments;
    private JPanel borderManualAssignments;
    private JScrollPane scManualAssignments;
    private ResourcePlanningTable tbManualAssignments;
    private JButton btnBookManually;
    private JButton btnRefreshManualAssignments;
    private JPanel borderWish;
    private JScrollPane scWish;
    private JTextArea taWish;
    private JPanel borderAvailablePositions;
    private JScrollPane scAvailablePositions;
    private ResourcePlanningTable tbAvailablePositions;
    private JButton btnReloadAvailablePositions;
    private JPanel pnlEventTuning;
    private JPanel borderPosAdding;
    private JScrollPane scPosAdding;
    private ResourcePlanningTable tbPosAdding;
    private JButton btnAddPosition;
    private JToolBar tbTuning;
    private JButton btnRefreshAddRemovePos;
    private JPanel borderPosRemoving;
    private JScrollPane scPosRemoving;
    private ResourcePlanningTable tbPosRemoving;
    private JButton btnRemovePosition;
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
    private JButton btnClipboard;
    private JLabel lblBody;
    private JScrollPane scBody;
    private JTextPane taBody;
    private JPanel pnlPostProcessing;
    private JPanel borderPostProcessingSource;
    private JScrollPane scPostProcessingSource;
    private TreeTable treeTablePostProcessingSource;
    private JToolBar tbPostProcessing;
    private JButton btnRefreshPostProcessing;
    private JButton btnSwapPositions;
    private JPanel borderPostProcessingTarget;
    private JScrollPane scPostProcessingTarget;
    private TreeTable treeTablePostProcessingTarget;
    private JProgressBar pgMain;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
