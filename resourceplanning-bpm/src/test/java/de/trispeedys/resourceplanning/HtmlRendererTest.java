package de.trispeedys.resourceplanning;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.interaction.JspRenderer;
import de.trispeedys.resourceplanning.util.TestUtil;

public class HtmlRendererTest
{
    // @Test
    public void testRenderPositionAvailableCallback()
    {
        TestUtil.clearAll();
        System.out.println(" --------- testRenderPositionUnavailableCallback --------- ");
        Helper helper = EntityFactory.buildHelper("Meier", "Klaus", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        System.out.println(JspRenderer.renderChosenPositionAvailableCallback(helper.getId(), null));
    }

    // @Test
    public void testRenderChosenPositionUnavailableCallback()
    {
        TestUtil.clearAll();
        System.out.println(" --------- testRenderChosenPositionUnavailableCallback --------- ");
        Helper helper = EntityFactory.buildHelper("Meier", "Klaus", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        System.out.println(JspRenderer.renderChosenPositionUnavailableCallback(helper.getId(), null));
    }
}