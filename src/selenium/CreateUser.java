package selenium;

import org.junit.Test;

public class CreateUser {

    private Base baseMethods ;

    public CreateUser(Base baseMethods) {
        this.baseMethods = baseMethods;
    }

    private Base getBaseMethods() {
        return baseMethods;
    }

    @Test
    public void createuser(String userName,String password,String email,String profile){

        openUserTab();

        openFormForNewUser();

        putUserData(
                userName==null||userName==""?"1new3_user":userName,
                password==null||password==""?"1new3_password":password,
                email==null||email==""?"1new3_email@softq.pl":email);

        selectProfile(profile==null||profile==""?"new_mateusz_profile":profile);

        runSaveActionForNewuser();

        checkFinalMessage();

    }
    private void openUserTab(){
        getBaseMethods().waitBy1Second();
        getBaseMethods().findElementByXPathAndClickAction(FinalVariables.LINK_BY_HREF_NEWUSER);
    }
    private void openFormForNewUser(){
        getBaseMethods().waitBy1Second();
        getBaseMethods().findImgElementByTitleAndClickAction(FinalVariables.ADD_NEW_USER);
    }
    private void putUserData(String userName, String password, String email){

        getBaseMethods().findElementByIdAndPutData("username",userName);
        getBaseMethods().findElementByIdAndPutData("password",password);
        getBaseMethods().findElementByIdAndPutData("email",email);
    }
    private void selectProfile(String selectProfileName){

        getBaseMethods().selectValueFromList("usersProfilesList",selectProfileName);
    }
    protected void runSaveActionForNewuser(){

        getBaseMethods().findImgElementByTitleAndClickAction(FinalVariables.SAVE_NEW_USER);
    }
    protected void checkFinalMessage(){
        getBaseMethods().waitBy3Second();
        if( getBaseMethods().checkfinalMessageAfterAction("td","formError",FinalVariables.FINAL_MESSAGE_USERHASBEENADDED)){
            //something wrong
        }
    }
}
