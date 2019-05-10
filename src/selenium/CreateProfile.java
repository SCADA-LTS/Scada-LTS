package selenium;

import org.junit.Test;

public class CreateProfile {

    Base baseMethods;

    public CreateProfile(Base baseMethods) {
        this.baseMethods = baseMethods;
    }

    private Base getBaseMethods() {
        return baseMethods;
    }

    @Test
    public void createProfile(String profileName){

        openProfileTab();

        openFormForNewProfile();

        putProfileName(profileName);

        setDataSource();

        setWatchList();

        setGraphicsView();

        runSaveActionForNewProfile();

        checkFinalMessage();



    }
    private void openProfileTab(){
        getBaseMethods().waitBy1Second();
        getBaseMethods().findElementByXPathAndClickAction(FinalVariables.LINK_BY_HREF_USERPROFILES);
    }
    private void openFormForNewProfile(){

        getBaseMethods().waitBy1Second();
        getBaseMethods().findImgElementByTitleAndClickAction(FinalVariables.ADD_NEW_PROFILE);
        getBaseMethods().waitBy1Second();
    }
    private void putProfileName(String profileName){

        getBaseMethods().findElementByIdAndPutData(FinalVariables.TEXTFIELD_USER_PROFILE_NAME,profileName);
    }
    private void runSaveActionForNewProfile(){
        getBaseMethods().waitBy1Second();
        getBaseMethods().findImgElementByTitleAndClickAction(FinalVariables.SAVE_NEW_PROFILE);
        getBaseMethods().waitBy2Second();
    }
    private void checkFinalMessage(){

        boolean result = getBaseMethods().checkfinalMessageAfterAction("td","formError",FinalVariables.FINAL_MESSAGE_PROFILEHASBEENADDED);
    }
    private void setDataSource(){
        getBaseMethods().findElementByIdAndClickAction("dp5/2");

        getBaseMethods().waitBy2Second();
    }
    private void setWatchList(){
        getBaseMethods().findElementByIdAndClickAction("wl1/2");

        getBaseMethods().waitBy2Second();
    }
    private void setGraphicsView(){
        getBaseMethods().findElementByIdAndClickAction("vw3/2");

        getBaseMethods().waitBy2Second();
    }
}
