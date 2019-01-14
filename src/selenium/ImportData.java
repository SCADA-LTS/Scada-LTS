package selenium;

import org.openqa.selenium.WebElement;

import java.io.*;
import java.util.List;

public class ImportData {

    private Base baseMethods ;

    public ImportData(Base baseMethods) {
        this.baseMethods = baseMethods;
    }

    private Base getBaseMethods() {
        return baseMethods;
    }

    void openImportExportTab(){

        getBaseMethods().waitBy2Second();

        getBaseMethods().findElementByXPathAndClickAction(FinalVariables.LINK_BY_HREF_IMPORTEXPORT);

    }

    void putDataForImport(){

        File file = new File(FinalVariables.PATH_TO_FILE_WITH_JSON_DATA);
        StringBuilder dataToImport=new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                dataToImport.append(st);
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }

        getBaseMethods().waitBy2Second();

        getBaseMethods().findElementByIdAndPutData("emportData",dataToImport.toString());

    }
    void startImport(){

        getBaseMethods().findElementByIdAndClickAction("importJsonBtn");

        boolean result = importComplete();

    }
    private boolean importComplete(){
        List<WebElement> list = getBaseMethods().findElementsByTagName("td");

        for(WebElement elements:list){
            String className = elements.getAttribute("id");
            String text = elements.getText();
            if(className.equals("alternateMessage"))
                if(text.equals("Import complete"))
                    return true;
        }

        return false;
    }



}

