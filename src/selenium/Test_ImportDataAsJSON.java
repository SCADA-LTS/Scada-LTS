package selenium;

import org.junit.Test;

public class Test_ImportDataAsJSON {

    AntSeleniumTest_LogIn_And_LogInWithFailUser logIn ;
    Test_LogOut logOut;
    ImportData importExportData;
    OpenCloseWebBrowser openCloseWebBrowser;

    @Test
    public void loginAsUserNameWhichExist(){

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(new Base());
        logIn.setUserName("admin");
        logIn.setSecret("admin");

        openCloseWebBrowser = new OpenCloseWebBrowser(logIn.getBaseMethods());
        openCloseWebBrowser.openWebBrowser();

        logIn = new AntSeleniumTest_LogIn_And_LogInWithFailUser(openCloseWebBrowser.getBaseMethods());
        logIn.loginAsUserNameWhichExistWithoutWebBrowserOpenClose();

        importExportData = new ImportData(openCloseWebBrowser.getBaseMethods());
        importExportData.openImportExportTab();
        importExportData.putDataForImport();
        importExportData.startImport();

        logOut = new Test_LogOut(openCloseWebBrowser.getBaseMethods());
        logOut.LogOut();

        openCloseWebBrowser.getBaseMethods().waitBy2Second();
        openCloseWebBrowser.closeBrowser();

    }

}
