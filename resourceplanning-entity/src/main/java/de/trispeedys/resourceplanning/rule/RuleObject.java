package de.trispeedys.resourceplanning.rule;

import java.util.List;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;

public abstract class RuleObject<T>
{
    // go and generate something
    public abstract List<T> generate(Helper helper, Event event);
}