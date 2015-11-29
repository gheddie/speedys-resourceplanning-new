package de.trispeedys.resourceplanning.util;

import de.trispeedys.resourceplanning.entity.Domain;

public class PositionInclude
{
    private Domain domain;
    
    private int positionNumber;

    public PositionInclude(Domain domain, int positionNumber)
    {
        super();
        this.domain = domain;
        this.positionNumber = positionNumber;
    }

    public int getPositionNumber()
    {
        return positionNumber;
    }
}