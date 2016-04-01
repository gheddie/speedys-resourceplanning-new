package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.ManualAssignmentComment;

public class ManualAssignmentCommentBuilder extends AbstractEntityBuilder<ManualAssignmentComment>
{
    private String comment;
    
    private GuidedEvent event;

    private Helper helper;

    public ManualAssignmentCommentBuilder withComment(String aComment)
    {
        comment = aComment;
        return this;
    }
    
    public ManualAssignmentCommentBuilder withEvent(GuidedEvent anEvent)
    {
        event = anEvent;
        return this;
    }
    
    public ManualAssignmentCommentBuilder withHelper(Helper aHelper)
    {
        helper = aHelper;
        return this;
    }
    
    public ManualAssignmentComment build()
    {
        ManualAssignmentComment manualAssignmentComment = new ManualAssignmentComment();
        manualAssignmentComment.setComment(comment);
        manualAssignmentComment.setEvent(event);
        manualAssignmentComment.setHelper(helper);
        return manualAssignmentComment;
    }
}