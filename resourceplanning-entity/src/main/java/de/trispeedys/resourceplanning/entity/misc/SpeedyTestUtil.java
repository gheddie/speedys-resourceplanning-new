package de.trispeedys.resourceplanning.entity.misc;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;


public class SpeedyTestUtil
{
    public static Domain buildDefaultDomain(int index)
    {
        return EntityFactory.buildDomain(
                "name_" + index,
                index).saveOrUpdate();
    }
}