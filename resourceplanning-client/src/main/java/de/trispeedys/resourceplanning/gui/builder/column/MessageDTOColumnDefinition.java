package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

public class MessageDTOColumnDefinition extends TableColumnDefinition
{
    protected List<TranslationItem> translations()
    {
        List<TranslationItem> map = new ArrayList<TranslationItem>();
        map.add(new TranslationItem("subject", "Thema"));
        map.add(new TranslationItem("messagingState", "Status"));
        map.add(new TranslationItem("recipient", "Empfänger"));
        map.add(new TranslationItem("body", "Text"));        
        map.add(new TranslationItem("errorMessage", "Fehler"));
        return map;
    }
}