package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class Base {

    WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    protected void waitBy(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void waitBy10Second() {
        waitBy5Second();
        waitBy5Second();
    }

    protected void waitBy5Second() {
        waitBy4Second();
        waitBy1Second();
    }

    protected void waitBy4Second() {
        waitBy3Second();
        waitBy1Second();
    }

    protected void waitBy3Second() {
        waitBy1Second();
        waitBy2Second();
    }

    protected void waitBy1Second() {
        waitBy(1000);
    }

    protected void waitBy2Second() {
        waitBy1Second();
        waitBy1Second();
    }
    protected List<WebElement> findElementsByTagName(String tagName){
        return getDriver().findElements(By.tagName(tagName));
    }
    private WebElement findElementById(String id) {
        return getDriver().findElement(By.id(id));
    }
    private WebElement findElementByXPath(String xPath) {
        return getDriver().findElement(By.xpath(xPath));
    }
    protected void findElementByIdAndPutData(String id,String data){

        findElementById(id).sendKeys(data);
    }

    protected void findElementByIdAndClickAction(String id){

        findElementById(id).click();
    }
    protected void findElementByXPathAndClickAction(String xPath){
        findElementByXPath(xPath).click();
    }
    protected void findImgElementByTitleAndClickAction(String title) {
        findElementByXPath(FinalVariables.LINK_BY_TITLE.replace(FinalVariables.TITLE_TO_REPLACE,title)).click();
    }
    protected boolean checkfinalMessageAfterAction(String tagName,String classNameText,String messageForUser){

        List<WebElement> list = findElementsByTagName(tagName);

        for(WebElement elements:list){
            String className = elements.getAttribute("class");
            String text = elements.getText();
            if(className.equals(classNameText))
                if(text.equals(messageForUser))
                    return false;
        }
    }



}
