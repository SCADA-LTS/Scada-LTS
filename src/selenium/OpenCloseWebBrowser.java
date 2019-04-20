package selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

class OpenCloseWebBrowser{

    Base baseMethods;

    public OpenCloseWebBrowser(Base baseMethods) {
        this.baseMethods = baseMethods;
    }

    public Base getBaseMethods() {
        return baseMethods;
    }

    public void setDriver(WebDriver driver){
        getBaseMethods().setDriver(driver);
    }

    public WebDriver getDriver(){

        return getBaseMethods().getDriver();
    }

    protected void waitBy(int time) {
        getBaseMethods().waitBy(time);
    }

    protected void openWebBrowser() {
        if (getDriver() == null) {
            setDriver(new FirefoxDriver());
        }

    }

    protected void closeBrowser() {

        if (getDriver() != null){
            getDriver().quit();
            setDriver(null);
        }

    }
}
