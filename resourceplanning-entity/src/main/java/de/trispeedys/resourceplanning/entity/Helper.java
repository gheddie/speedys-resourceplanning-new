package de.trispeedys.resourceplanning.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.HelperState;

@Entity
public class Helper extends Person
{
    public static final String TEST_MAIL_ADDRESS = "testhelper1.trispeedys@gmail.com";

    public static final String ATTR_HELPER_STATE = "helperState";

    @Enumerated(EnumType.STRING)
    @Column(name = "helper_state")
    @NotNull
    private HelperState helperState;

    @NotNull
    private String code;

    private boolean internal;

    public HelperState getHelperState()
    {
        return helperState;
    }

    public void setHelperState(HelperState helperState)
    {
        this.helperState = helperState;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public boolean isActive()
    {
        return (helperState.equals(HelperState.ACTIVE));
    }

    public boolean isAssignableTo(Position pos, Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateOfBirth());
        cal.add(Calendar.YEAR, pos.getMinimalAge());
        Date added = cal.getTime();
        return (date.after(added));
    }

    public boolean isInternal()
    {
        return this.internal;
    }

    public void setInternal(boolean internal)
    {
        this.internal = internal;
    }
}