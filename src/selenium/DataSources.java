package selenium;

public class DataSources {

    private Base baseMethods ;

    private String
            PREFIX_FOR_AHREF ="//a[@href='",
            DATASOURCEEDIT="data_source_edit",
            SELECTED_DATABASE="",
            LINK_HREF_DATASOURCEEDIT="",
            SUFIX_SHTML =".shtm",
            END="']";

    {
        SELECTED_DATABASE = "?dsid=3";
        LINK_HREF_DATASOURCEEDIT=PREFIX_FOR_AHREF+DATASOURCEEDIT+SUFIX_SHTML+SELECTED_DATABASE+END;
    }

    public DataSources(Base baseMethods) {
        this.baseMethods = baseMethods;
    }

    private Base getBaseMethods() {
        return baseMethods;
    }

    public void edit(){

        openTab();

        editSelected();

        selectPoint();

        selectTypeOfEventDetectorAndRunAddAction();

        saveEventDetectorAndCheckThatEventHasBeenSavedCorrectly();

    }
    private void openTab(){

        getBaseMethods().waitBy1Second();
        getBaseMethods().findElementByXPathAndClickAction(FinalVariables.LINK_BY_HREF_DATASOURCES);
        getBaseMethods().waitBy1Second();

    }
    private void editSelected(){

        getBaseMethods().findElementByXPathAndClickAction(LINK_HREF_DATASOURCEEDIT);
        getBaseMethods().waitBy1Second();

    }
    private void selectPoint(){

        getBaseMethods().waitBy1Second();
        getBaseMethods().findImgElementByOnClickValue("window.location='data_point_edit.shtm?dpid=5'");
        getBaseMethods().waitBy1Second();

    }
    private void selectTypeOfEventDetectorAndRunAddAction(){

        getBaseMethods().selectValueFromList("eventDetectorSelect","Change");
        getBaseMethods().findImgElementByOnClickValue("pointEventDetectorEditor.addEventDetector();");

    }
    private void saveEventDetectorAndCheckThatEventHasBeenSavedCorrectly() {

        getBaseMethods().waitBy1Second();
        getBaseMethods().findInputElementWithTypeSubmit("Save");
        getBaseMethods().waitBy1Second();

        //checking

        getBaseMethods().waitBy1Second();
        getBaseMethods().checkfinalMessageAfterAction("td","formError",FinalVariables.FINAL_MESSAGE_DATAPOINTHASBEENSAVED);
        getBaseMethods().waitBy1Second();

    }
}
