package selenium;

import org.junit.Test;

public class Test_CreateUser {


    private AntSeleniumTest_LogIn_And_LogInWithFailUser logIn;
    private Test_LogOut logOut;
    private OpenCloseWebBrowser openCloseWebBrowser;
    private CreateUser createProfile;

    @Test
    public void createProfile(){

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(new Base());
        logIn.setUserName("admin");
        logIn.setSecret("admin");

        openCloseWebBrowser = new OpenCloseWebBrowser(logIn.getBaseMethods());
        openCloseWebBrowser.openWebBrowser();

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(logIn.getBaseMethods());
        logIn.loginAsUserNameWhichExistWithoutWebBrowserOpenClose();

        createProfile = new CreateUser(logIn.getBaseMethods());
        createProfile.createuser("new3_user","new3_password","new3_email@softq.pl","new_mateusz_profile");

        logOut = new Test_LogOut(logIn.getBaseMethods());
        logOut.LogOut();

        openCloseWebBrowser.closeBrowser();

    }
}
