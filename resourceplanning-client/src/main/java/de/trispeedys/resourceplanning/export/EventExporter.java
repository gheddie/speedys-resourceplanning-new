package de.trispeedys.resourceplanning.export;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontSelector;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.trispeedys.resourceplanning.singleton.AppSingleton;
import de.trispeedys.resourceplanning.webservice.HierarchicalEventItemDTO;

public class EventExporter
{
    private static final Color COLOR_EVENT = new Color(128, 128, 128);
    private static final Color COLOR_DOMAIN = new Color(192, 192, 192);
    private static final Color COLOR_POSITION = new Color(255, 255, 255);
    
    private static final Font FONT_EVENT = new Font(BaseFont.SUPERSCRIPT_OFFSET, 12);
    private static final Font FONT_DOMAIN = new Font(BaseFont.SUPERSCRIPT_OFFSET, 10);
    private static final Font FONT_POSITION = new Font(BaseFont.SUPERSCRIPT_OFFSET, 8);
    
    private long eventId;
    
    public EventExporter(Long aEventId)
    {
        super();
        this.eventId = aEventId;        
    }

    public void export(String filename) throws FileNotFoundException, DocumentException
    {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        document.add(createTable());
        document.close();        
    }
    
    private PdfPTable createTable() {
        PdfPTable table = new PdfPTable(2);
        PdfPCell cellA = null;
        PdfPCell cellB = null;
        FontSelector selector = null;        
        Phrase phraseA = null;
        Phrase phraseB = null;
        for (HierarchicalEventItemDTO node : AppSingleton.getInstance().getPort().getEventNodes(eventId, false).getItem())
        {    
            switch (node.getItemType())
            {
                case "EVENT":
                    selector = new FontSelector();
                    selector.addFont(FONT_EVENT);
                    phraseA = selector.process(node.getInfoString());
                    cellA = new PdfPCell(phraseA);            
                    cellA.setBackgroundColor(COLOR_EVENT);                        
                    cellA.setColspan(2);
                    table.addCell(cellA);
                    break;
                case "DOMAIN":
                    selector = new FontSelector();
                    selector.addFont(FONT_DOMAIN);
                    phraseA = selector.process(node.getInfoString());
                    cellA = new PdfPCell(phraseA);            
                    cellA.setBackgroundColor(COLOR_DOMAIN);                        
                    cellA.setColspan(2);
                    table.addCell(cellA);
                    break;
                case "POSITION":
                    selector = new FontSelector();
                    selector.addFont(FONT_POSITION);
                    phraseA = selector.process(node.getInfoString());
                    cellA = new PdfPCell(phraseA);            
                    phraseB = selector.process(node.getAssignmentString());
                    cellB = new PdfPCell(phraseB);
                    cellA.setBackgroundColor(COLOR_POSITION);                        
                    cellB.setBackgroundColor(COLOR_POSITION);
                    cellA.setColspan(1);
                    cellB.setColspan(1);
                    table.addCell(cellA);
                    table.addCell(cellB);            
                    break;                    
            }
        }        
        return table;
    }    
    
    // ---
    
    public static void main(String[] args)
    {
        try
        {
            new EventExporter(new Long(13922)).export("D:\\export\\result_"+System.currentTimeMillis()+".pdf");
        }
        catch (FileNotFoundException | DocumentException e)
        {
            e.printStackTrace();
        }
    }
}