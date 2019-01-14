package selenium;


import org.junit.Test;

public class Test_CreateEventDetector {

    AntSeleniumTest_LogIn_And_LogInWithFailUser logIn ;
    Test_LogOut logOut;
    DataSources ds;
    OpenCloseWebBrowser openCloseWebBrowser;

    @Test
    public void createEventDetector(){

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(new Base());
        logIn.setUserName("admin");
        logIn.setSecret("admin");

        openCloseWebBrowser = new OpenCloseWebBrowser(logIn.getBaseMethods());
        openCloseWebBrowser.openWebBrowser();

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(openCloseWebBrowser.getBaseMethods());
        logIn.loginAsUserNameWhichExistWithoutWebBrowserOpenClose();

        ds = new DataSources(openCloseWebBrowser.getBaseMethods());
        ds.edit();

        logOut = new Test_LogOut(openCloseWebBrowser.getBaseMethods());
        logOut.LogOut();

        openCloseWebBrowser.getBaseMethods().waitBy2Second();
        openCloseWebBrowser.closeBrowser();

    }

}
