package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

public class RequestedSwapDTOColumnDefinition extends TableColumnDefinition
{
    protected List<TranslationItem> translations()
    {
        List<TranslationItem> map = new ArrayList<TranslationItem>();
        map.add(new TranslationItem("sourceDomain", "Bereich von"));
        map.add(new TranslationItem("sourceHelper", "Helfer von"));
        map.add(new TranslationItem("sourcePosition", "Position von"));
        map.add(new TranslationItem("swapState", "Status"));
        map.add(new TranslationItem("swapType", "Typ"));
        map.add(new TranslationItem("targetDomain", "Bereich nach"));
        map.add(new TranslationItem("targetHelper", "Helfer nach"));
        map.add(new TranslationItem("targetPosition", "Position nach"));
        return map;
    }
}