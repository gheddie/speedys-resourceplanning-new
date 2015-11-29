package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.interaction.HtmlRenderer;

public class HtmlRendererTest
{
    // @Test
    public void testRenderPositionAvailableCallback()
    {
        HibernateUtil.clearAll();
        System.out.println(" --------- testRenderPositionUnavailableCallback --------- ");
        Helper helper = EntityFactory.buildHelper("Meier", "Klaus", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        System.out.println(HtmlRenderer.renderChosenPositionAvailableCallback(helper.getId(), null));
    }

    // @Test
    public void testRenderChosenPositionUnavailableCallback()
    {
        HibernateUtil.clearAll();
        System.out.println(" --------- testRenderChosenPositionUnavailableCallback --------- ");
        Helper helper = EntityFactory.buildHelper("Meier", "Klaus", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        System.out.println(HtmlRenderer.renderChosenPositionUnavailableCallback(helper.getId(), null));
    }
}