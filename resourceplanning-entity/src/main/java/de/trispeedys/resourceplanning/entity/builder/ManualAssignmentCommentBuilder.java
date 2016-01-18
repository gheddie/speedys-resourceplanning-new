package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.ManualAssignmentComment;
import de.trispeedys.resourceplanning.util.StringUtil;

public class ManualAssignmentCommentBuilder extends AbstractEntityBuilder<ManualAssignmentComment>
{
    private static final int MAX_COMMENT_LENGTH = 250;

    private String comment;
    
    private Event event;

    private Helper helper;

    public ManualAssignmentCommentBuilder withComment(String aComment)
    {
        if ((!(StringUtil.isBlank(aComment))) && (aComment.length() > MAX_COMMENT_LENGTH))
        {
            aComment = aComment.substring(0, MAX_COMMENT_LENGTH);
        }
        comment = aComment;
        return this;
    }
    
    public ManualAssignmentCommentBuilder withEvent(Event anEvent)
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