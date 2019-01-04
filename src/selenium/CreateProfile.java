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
    public void createProfile(){

        openProfileTab();

        openFormForNewProfile();

        putProfileName();

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
        getBaseMethods().waitBy10Second();
    }
    private void putProfileName(){

        getBaseMethods().findElementByIdAndPutData(FinalVariables.TEXTFIELD_USER_PROFILE_NAME,"new_mateusz_profile");
    }
    private void runSaveActionForNewProfile(){
        getBaseMethods().waitBy1Second();
        getBaseMethods().findImgElementByTitleAndClickAction(FinalVariables.SAVE_NEW_PROFILE);
        getBaseMethods().waitBy10Second();
    }
    private void checkFinalMessage(){

        boolean result = getBaseMethods().checkfinalMessageAfterAction("td","formError",FinalVariables.FINAL_MESSAGE_PROFILEHASBEENADDED);
    }
}
