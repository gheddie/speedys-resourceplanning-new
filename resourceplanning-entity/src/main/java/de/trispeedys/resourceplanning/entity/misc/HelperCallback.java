package de.trispeedys.resourceplanning.entity.misc;

public enum HelperCallback
{
    /** some procedure as last year... */
    ASSIGNMENT_AS_BEFORE("Ich m�chte auf derselben Position helfen", "Zuweisung wie zuvor"),
    
    /** not this time... */
    PAUSE_ME("Diesmal habe ich keine Zeit zum Helfen", "Diesmal keine Verwendung"),
    
    /** no more helping ever... */
    QUIT_FOREVER("Ich m�chte nicht mehr bel�stigt werden", "keine Verwendung mehr (f�r immer)"),    
    
    /** i want to do something else this time... */
    CHANGE_POS("Ich m�chte auf einer anderen Position helfen", "Wahl einer neuen Position"),
    
    /** i want to be manually assigned... */
    ASSIGN_ME_MANUALLY("Ich m�chte nach Absprache eine Position zugewiesen bekommen", "Zuweisung einer Position");
    
    private String description;
    
    private String summary;
    
    HelperCallback(String aDescription, String aSummary)
    {
        this.description = aDescription;
        this.summary = aSummary;
    }

    public String getDescription()
    {
        return description;
    }

    public String getSummary()
    {
        return summary;
    }
}