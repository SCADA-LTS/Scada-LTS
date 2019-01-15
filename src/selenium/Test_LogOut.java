package selenium;

import org.junit.Test;

public class Test_LogOut {

    Base baseMethods;
    OpenCloseWebBrowser openCloseWebBrowser;

    public Test_LogOut(Base baseMEthods) {
        this.baseMethods = baseMEthods;
    }

    public Base getBaseMethods() {
        return baseMethods;
    }

    @Test
    public void LogOut(){

        getBaseMethods().findElementByXPathAndClickAction(FinalVariables.LINK_BY_HREF_LOGOUT);

    }
}
