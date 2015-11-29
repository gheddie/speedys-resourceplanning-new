package de.trispeedys.resourceplanning.gui.builder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.trispeedys.resourceplanning.gui.builder.column.EventDTOColumnDefinition;
import de.trispeedys.resourceplanning.gui.builder.column.ExecutionDTOColumnDefinition;
import de.trispeedys.resourceplanning.gui.builder.column.HelperDTOColumnDefinition;
import de.trispeedys.resourceplanning.gui.builder.column.ManualAssignmentDTOColumnDefinition;
import de.trispeedys.resourceplanning.gui.builder.column.PositionDTOColumnDefinition;
import de.trispeedys.resourceplanning.gui.builder.column.TableColumnDefinition;
import de.trispeedys.resourceplanning.util.converter.ConverterUtil;
import de.trispeedys.resourceplanning.webservice.EventDTO;
import de.trispeedys.resourceplanning.webservice.ExecutionDTO;
import de.trispeedys.resourceplanning.webservice.HelperDTO;
import de.trispeedys.resourceplanning.webservice.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.webservice.PositionDTO;

public class TableModelBuilder
{
    private static HashMap<Class<?>, TableColumnDefinition> columnDefinitions = new HashMap<Class<?>, TableColumnDefinition>();
    static
    {
        columnDefinitions.put(EventDTO.class, new EventDTOColumnDefinition());
        columnDefinitions.put(HelperDTO.class, new HelperDTOColumnDefinition());
        columnDefinitions.put(PositionDTO.class, new PositionDTOColumnDefinition());
        columnDefinitions.put(ManualAssignmentDTO.class, new ManualAssignmentDTOColumnDefinition());
        columnDefinitions.put(ExecutionDTO.class, new ExecutionDTOColumnDefinition());
    }

    public static TableModel createGenericTableModel(List<?> objects)
    {
        try
        {
            if ((objects == null) || (objects.size() == 0))
            {
                return new DefaultTableModel();
            }

            Class<? extends Object> clazz = objects.get(0).getClass();
            
            TableColumnDefinition definition = columnDefinitions.get(clazz);
            if (definition != null)
            {
                // got a column definition
                int fieldCount = definition.getColumnCount();

                Object[] headers = new Object[fieldCount];
                int headerIndex = 0;
                for (String fieldName : definition.getFieldNames())
                {
                    headers[headerIndex] = definition.translate(fieldName);
                    headerIndex++;
                }
                Object[][] data = new Object[objects.size()][fieldCount];
                int rowIndex = 0;
                for (Object obj : objects)
                {
                    Field field = null;
                    int colIndex = 0;
                    for (String fieldName : definition.getFieldNames())
                    {
                        field = clazz.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        data[rowIndex][colIndex] = ConverterUtil.convert(field.get(obj));
                        colIndex++;
                    }
                    rowIndex++;
                }
                return new DefaultTableModel(data, headers);   
            }
            else
            {
                // no column definition                
                int fieldCount = clazz.getDeclaredFields().length;

                Object[] headers = new Object[fieldCount];
                for (int headerIndex = 0; headerIndex < fieldCount; headerIndex++)
                {
                    headers[headerIndex] = clazz.getDeclaredFields()[headerIndex].getName();
                }
                Object[][] data = new Object[objects.size()][fieldCount];
                int rowIndex = 0;
                for (Object obj : objects)
                {
                    Field field = null;
                    for (int colIndex = 0; colIndex < fieldCount; colIndex++)
                    {
                        field = clazz.getDeclaredFields()[colIndex];
                        field.setAccessible(true);
                        data[rowIndex][colIndex] = ConverterUtil.convert(field.get(obj));
                    }
                    rowIndex++;
                }
                return new DefaultTableModel(data, headers);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}