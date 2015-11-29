package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

import de.trispeedys.resourceplanning.webservice.ExecutionDTO;

public class ExecutionDTOColumnDefinition extends TableColumnDefinition
{
    protected List<TranslationItem> translations()
    {
        List<TranslationItem> map = new ArrayList<TranslationItem>();
        map.add(new TranslationItem("helperLastName", "Nachname"));
        map.add(new TranslationItem("helperFirstName", "Vorname"));
        map.add(new TranslationItem("waitState", "Objekt"));
        map.add(new TranslationItem("additionalInfo", "Ausprägung"));
        return map;
    }
}