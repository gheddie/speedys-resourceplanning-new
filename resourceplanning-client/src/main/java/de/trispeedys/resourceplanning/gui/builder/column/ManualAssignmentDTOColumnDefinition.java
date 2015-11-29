package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

import de.trispeedys.resourceplanning.webservice.ManualAssignmentDTO;

public class ManualAssignmentDTOColumnDefinition extends TableColumnDefinition
{
    protected List<TranslationItem> translations()
    {
        List<TranslationItem> map = new ArrayList<TranslationItem>();
        map.add(new TranslationItem("helperName", "Helfer"));
        return map;
    }
}