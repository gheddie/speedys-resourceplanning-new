package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.misc.SwapResult;

public class InterruptSwapDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        AssignmentSwap swap = getSwapEntity(execution);
        swap.setSwapResult(SwapResult.INTERRUPTED);
        swap.saveOrUpdate();
    }
}