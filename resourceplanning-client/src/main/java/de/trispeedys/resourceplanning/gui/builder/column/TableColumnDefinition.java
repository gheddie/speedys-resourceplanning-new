package de.trispeedys.resourceplanning.gui.builder.column;

import java.util.ArrayList;
import java.util.List;

public abstract class TableColumnDefinition
{
    private List<TranslationItem> translationMap;

    public int getColumnCount()
    {
        return getTranslations().size();
    }

    protected abstract List<TranslationItem> translations();

    public Object translate(String key)
    {
        for (TranslationItem item : translationMap)
        {
            if (item.getUntranslatedValue().equals(key))
            {
                return item.getTranslatedValue();
            }
        }
        return null;
    }

    private List<TranslationItem> getTranslations()
    {
        if (translationMap == null)
        {
            translationMap = translations();
        }
        return translationMap;
    }

    public List<String> getFieldNames()
    {
        List<String> result = new ArrayList<String>();
        for (TranslationItem item : translationMap)
        {
            result.add(item.getUntranslatedValue());
        }
        return result;
    }
}