package selenium;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

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
    private static String URL = "localhost:" + PORT;
    private static String SCADA_PAGE = "login.htm";
    private static String SCADA_APPLICATION = "ScadaBR";
    private static String PROTOCOL = "http";
    private static String SCADALTS = PROTOCOL + "://" + URL + "/" + SCADA_APPLICATION + "/" + SCADA_PAGE;

    private static String USERNAME = null;
    private static String SECRET = null;

    private OpenCloseWebBrowser openCloseWebBrowser;
    private Base baseMethods;

    //*******************
    //settters -- getters

    public Base getBaseMethods() {
        return baseMethods;
    }

    protected void setUserName(String userName){

        this.USERNAME = userName;
    }
    protected void setSecret(String secret){

        this.SECRET = secret;
    }
    protected String getUserName(){
        return this.USERNAME;
    }
    protected String getSecret(){

        return this.SECRET;
    }

    //end settters -- getters
    //*******************


    public AntSeleniumTest_LogIn_And_LogInWithFailUser(Base baseMethods) {
        this.baseMethods = baseMethods;
    }

    private void setUpEnvironment() {
        openCloseWebBrowser = new OpenCloseWebBrowser(new Base());
        openCloseWebBrowser.openWebBrowser();
        getBaseMethods().setDriver(openCloseWebBrowser.getDriver());

    }
    private void turnOffEnvironment(){
        getBaseMethods().setDriver(null);
        openCloseWebBrowser.closeBrowser();
    }

    public void loginAsUserNameWhichExistWithoutWebBrowserOpenClose(){
        if(USERNAME == null) {
            setUserName("admin");
            setSecret("admin");
        }

        openLoginPageAndLogInAction();

        getBaseMethods().waitBy2Second();
    }
    @Test
    public void loginAsUserNameWhichExist() {

        setUpEnvironment();

        loginAsUserNameWhichExistWithoutWebBrowserOpenClose();


        turnOffEnvironment();

    }
    @Test
    public void loginAsUserNameWhichNotExist() {

        setUpEnvironment();

        if(USERNAME == null) {
            setUserName("admin2");
            setSecret("admin");
        }
        openLoginPageAndLogInAction();

        turnOffEnvironment();

    }


    private boolean openLoginPageAndLogInAction(){

        getBaseMethods().getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        getBaseMethods().getDriver().get(SCADALTS);

        getBaseMethods().waitBy2Second();
        logIn(getUserName(),getSecret());
        getBaseMethods().waitBy2Second();
        boolean userLoggedIn =checkThatUserExist();

        return userLoggedIn;

    }
    private void logIn(String userName,String password){
        LOG.info("login action");
        String USERNAME_FIELD = "username";
        String PASSWORD_FIELD = "password";
        String LOGIN_BUTTON = "login";

        getBaseMethods().findElementByIdAndPutData(USERNAME_FIELD,userName);
        getBaseMethods().waitBy1Second();

        getBaseMethods().findElementByIdAndPutData(PASSWORD_FIELD,password);
        getBaseMethods().waitBy1Second();

        getBaseMethods().findElementByIdAndClickAction(LOGIN_BUTTON);
        LOG.info("login action done");
    }
    private boolean checkThatUserExist(){

        boolean finalResult = getBaseMethods().checkfinalMessageAfterAction("div","formError",FinalVariables.FINAL_MESSAGE_CANNOTFINDUSER);

        return finalResult;

    }


}
