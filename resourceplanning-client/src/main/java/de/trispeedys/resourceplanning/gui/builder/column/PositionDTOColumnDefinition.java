package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

public class PositionDTOColumnDefinition extends TableColumnDefinition
{
    protected List<TranslationItem> translations()
    {
        List<TranslationItem> map = new ArrayList<TranslationItem>();
        map.add(new TranslationItem("description", "Beschreibung"));
        map.add(new TranslationItem("minimalAge", "Mindestalter"));
        map.add(new TranslationItem("domain", "Bereich"));
        return map;
    }
}