package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

import de.trispeedys.resourceplanning.webservice.HelperDTO;

public class HelperDTOColumnDefinition extends TableColumnDefinition
{
    protected List<TranslationItem> translations()
    {
        List<TranslationItem> map = new ArrayList<TranslationItem>();
        map.add(new TranslationItem("firstName", "Vorname"));
        map.add(new TranslationItem("lastName", "Nachname"));
        map.add(new TranslationItem("email", "Mail"));
        map.add(new TranslationItem("helperState", "Status"));
        return map;
    }
}