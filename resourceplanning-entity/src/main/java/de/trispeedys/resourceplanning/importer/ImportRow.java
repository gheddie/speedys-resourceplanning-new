package de.trispeedys.resourceplanning.importer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportRow
{
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    
    private String helperLastName;

    private String helperFirstName;
    
    private Date dateOfBirth;

    private String helperMail;

    private int domainNumber;

    private String domainName;

    private String position;

    private int positionNumber;

    public ImportRow(String helperLastName, String helperFirstName, String dateOfBirth, String helperMail, String domainNumber, String domainName, String positionNumber,
            String position)
    {
        this.helperLastName = helperLastName.trim();
        this.helperFirstName = helperFirstName.trim();
        this.dateOfBirth = parseDate(dateOfBirth);
        this.helperMail = helperMail.trim();
        this.domainNumber = Integer.parseInt(domainNumber.trim());
        this.domainName = domainName.trim();
        this.positionNumber = Integer.parseInt(positionNumber.trim());
        this.position = position.trim();
    }

    private Date parseDate(String dateOfBirth)
    {
        try
        {
            return df.parse(dateOfBirth);
        }
        catch (ParseException e)
        {
            System.out.println(" ### ERROR ### : " + e.getMessage());
            return null;
        }
    }

    public void debug()
    {
        System.out.println("------------------------------");
        System.out.println("helperLastName : #" + helperLastName + "#");
        System.out.println("helperFirstName : #" + helperFirstName + "#");
        System.out.println("domainNumber : #" + domainNumber + "#");
        System.out.println("domainName : #" + domainName + "#");
        System.out.println("position : #" + position + "#");
        System.out.println("positionNumber : #" + positionNumber + "#");
    }

    public String getHelperLastName()
    {
        return helperLastName;
    }

    public String getHelperFirstName()
    {
        return helperFirstName;
    }
    
    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }

    public String getHelperMail()
    {
        return helperMail;
    }

    public int getDomainNumber()
    {
        return domainNumber;
    }

    public String getDomainName()
    {
        return domainName;
    }

    public String getPosition()
    {
        return position;
    }

    public int getPositionNumber()
    {
        return positionNumber;
    }
}