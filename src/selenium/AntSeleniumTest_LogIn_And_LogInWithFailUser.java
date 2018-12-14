package selenium;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.scada_lts.dao.DAO;

/**
 * Class responsible for selenium test with two cases
 * 1 -> LogIn with username who exist
 * 2 -> LogIn with username who not exist
 *
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl ; hyski.mateusz@gmail.com
 *
 */
public class AntSeleniumTest_LogIn_And_LogInWithFailUser {

    private static final Log LOG = LogFactory.getLog(AntSeleniumTest_LogIn_And_LogInWithFailUser.class);

    private static String PORT = "8080";
    private static String URL = "localhost:"+PORT;
    private static String SCADA_PAGE = "login.htm";
    private static String SCADA_APPLICATION = "ScadaBR";
    private static String PROTOCOL= "http";
    private static String SCADALTS = PROTOCOL+"://"+URL+"/"+SCADA_APPLICATION+"/"+SCADA_PAGE;

    private static String USERNAME=null;
    private static String SECRET=null;

    WebDriver driver;

    @Test
    public void loginAsUserNameWhichExist() {

        USERNAME = "admin";
        SECRET = "admin";

        openWebBrowser();

        openLoginPageAndLogInAction();

        //logOut();

        closeBrowser();

    }
    @Test
    public void loginAsUserNameWhichNotExist() {

        USERNAME = "admin2";
        SECRET = "admin";

        openWebBrowser();

        openLoginPageAndLogInAction();

        //logOut();

        closeBrowser();

    }
    private void openWebBrowser(){

        driver = new FirefoxDriver();

    }

    private void closeBrowser(){

        driver.quit();

    }

    private boolean openLoginPageAndLogInAction(){

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(SCADALTS);

        logIn(USERNAME,SECRET);

        boolean userLoggedIn =checkThatUserExist();

        return userLoggedIn;

    }
    private void logIn(String userName,String password){

        String USERNAME_FIELD = "username";
        String PASSWORD_FIELD = "password";
        String LOGIN_BUTTON = "login";

        driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
        driver.findElement(By.id(USERNAME_FIELD)).sendKeys(userName);

        driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
        driver.findElement(By.id(PASSWORD_FIELD)).sendKeys(password);

        driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
        driver.findElement(By.id(LOGIN_BUTTON)).click();
    }
    private boolean checkThatUserExist(){

        driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.tagName("div"));

        for(WebElement elements:list){
            String className = elements.getAttribute("class");
            String text = elements.getText();
            if(className.equals("formError"))
                if(text.equals("Cannot find user Id"))
                    return false;
        }

        return true;
    }
}
