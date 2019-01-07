package selenium;

import org.junit.Test;

public class Test_CreateProfile {

    private AntSeleniumTest_LogIn_And_LogInWithFailUser logIn;
    private Test_LogOut logOut;
    private OpenCloseWebBrowser openCloseWebBrowser;
    private CreateProfile createProfile;

    @Test
    public void createProfile(){

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(new Base());
        logIn.setUserName("admin");
        logIn.setSecret("admin");

        openCloseWebBrowser = new OpenCloseWebBrowser(logIn.getBaseMethods());
        openCloseWebBrowser.openWebBrowser();

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(logIn.getBaseMethods());
        logIn.loginAsUserNameWhichExistWithoutWebBrowserOpenClose();

        createProfile = new CreateProfile(logIn.getBaseMethods());
        createProfile.createProfile("new2_mateusz_profile");

        logOut = new Test_LogOut(logIn.getBaseMethods());
        logOut.LogOut();

        openCloseWebBrowser.closeBrowser();

    }
}
