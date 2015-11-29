package de.trispeedys.resourceplanning.gui.builder.column;

public class TranslationItem
{
    private String translatedValue;
    
    private String untranslatedValue;

    public TranslationItem(String untranslatedValue, String translatedValue)
    {
        super();
        this.translatedValue = translatedValue;
        this.untranslatedValue = untranslatedValue;
    }

    public String getTranslatedValue()
    {
        return translatedValue;
    }
    
    public String getUntranslatedValue()
    {
        return untranslatedValue;
    }
}