package de.trispeedys.resourceplanning.export;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import de.trispeedys.resourceplanning.singleton.AppSingleton;
import de.trispeedys.resourceplanning.webservice.HierarchicalEventItemDTO;

public class EventExporter
{
    private static final BaseColor COLOR_EVENT = new BaseColor(128, 128, 128);
    private static final BaseColor COLOR_DOMAIN = new BaseColor(192, 192, 192);
    private static final BaseColor COLOR_POSITION = new BaseColor(255, 255, 255);
    
    private static final Font FONT_EVENT = new Font(FontFamily.HELVETICA, 12);
    private static final Font FONT_DOMAIN = new Font(FontFamily.HELVETICA, 10);
    private static final Font FONT_POSITION = new Font(FontFamily.HELVETICA, 8);
    
    private long eventId;
    
    private int positionCount;
    
    private int assignmentCount;
    
    public EventExporter(Long aEventId, int aPositionCount, int anAssignmentCount)
    {
        super();
        this.eventId = aEventId;
        this.positionCount = aPositionCount;
        this.assignmentCount = anAssignmentCount;
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
                    phraseA = selector.process(node.getInfoString() + " ["+assignmentCount+" von "+positionCount+" Pos. besetzt]");
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
}