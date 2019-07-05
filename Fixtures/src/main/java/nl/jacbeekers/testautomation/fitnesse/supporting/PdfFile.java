package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.io.IOException;

//import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PdfFile {

    private String returnMessage= Constants.OK;
        
    public void createPDF(String filePath, String outputFileName, PDPage [] pages) {
        // Create a new empty PDF document
        PDDocument pdfDoc = null;
        
        try {
            pdfDoc=new PDDocument();

            // Add pages to PDF document
            for(int i = 0; i < pages.length; i++) {
                 pdfDoc.addPage( pages[i] );
             }

            pdfDoc.save(filePath);
        }
        catch (  IOException e2) {
            e2.printStackTrace();
            setReturnMessage("Error creating empty PDF file - " + e2.toString());      
        }
        finally {
          if (pdfDoc != null) {
            try {
              pdfDoc.close();
            }
            catch ( IOException e3) {
                e3.printStackTrace();
                setReturnMessage("Error closing PDF file - " + e3.toString());       
                }
          }
        }        
    }

    public void setReturnMessage(String returnMessage){
            this.returnMessage = returnMessage;
    }
    
    public String getReturnMessage(){
            return returnMessage;
    }

}
