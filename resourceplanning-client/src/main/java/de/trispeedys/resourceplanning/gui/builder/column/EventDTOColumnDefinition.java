package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

public class EventDTOColumnDefinition extends TableColumnDefinition
{
    protected List<TranslationItem> translations()
    {
        List<TranslationItem> map = new ArrayList<TranslationItem>();
        map.add(new TranslationItem("description", "Beschreibung"));
        map.add(new TranslationItem("eventState", "Status"));
        map.add(new TranslationItem("eventDate", "Datum"));
        return map;
    }
}