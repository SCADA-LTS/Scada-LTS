package selenium;

class FinalVariables {

    private static final String PREFIX_FOR_AHREF ="//a[@href='";
    private static final String PREFIX_FOR_IMGTITLE ="//img[contains(@title,'TITLE_TO_REPLACE')]";
    private static final String PREFIX_FOR_IMGONCLICK="//img[@onclick=\"ONCLICK_VALUE\"]";
    private static final String PREFIX_FOR_INPUTVALUE="input[value=";
    private static final String SUFIX_SHTML =".shtm']";
    private static final String SUFIX_HTM =".htm']";
    private static final String USER_PROFILES = "usersProfiles";
    private static final String NEW_USER = "users";
    private static final String DATA_SOURCES = "data_sources";
    private static final String IMPORT_EXPORT= "emport";
    private static final String LOGOUT= "logout";

    public static final String FINAL_MESSAGE_PROFILEHASBEENADDED ="User profile added.";
    public static final String FINAL_MESSAGE_USERHASBEENADDED ="User added.";
    public static final String FINAL_MESSAGE_DATAPOINTHASBEENSAVED ="Data point has been saved";
    public static final String FINAL_MESSAGE_CANNOTFINDUSER = "Cannot find user Id";
    public static final String TEXTFIELD_USER_PROFILE_NAME = "userProfileName";

    public static final String SAVE_NEW_PROFILE="Save";
    public static final String SAVE_NEW_USER=SAVE_NEW_PROFILE;

    public static final String ADD_NEW_PROFILE="Add user profile";
    public static final String ADD_NEW_USER="Add user";

    public static final String TITLE_TO_REPLACE="TITLE_TO_REPLACE";
    public static final String ONCLICK_VALUE="ONCLICK_VALUE";
    public static final String LINK_BY_ONCLICKVALUE = PREFIX_FOR_IMGONCLICK;
    public static final String LINK_BY_TITLE = PREFIX_FOR_IMGTITLE;
    public static final String LINK_BY_INPUTVALUE= PREFIX_FOR_INPUTVALUE;

    public static final String LINK_BY_HREF_DATASOURCES = PREFIX_FOR_AHREF +DATA_SOURCES+ SUFIX_SHTML;
    public static final String LINK_BY_HREF_NEWUSER = PREFIX_FOR_AHREF +NEW_USER+ SUFIX_SHTML;
    public static final String LINK_BY_HREF_USERPROFILES = PREFIX_FOR_AHREF +USER_PROFILES+ SUFIX_SHTML;
    public static final String LINK_BY_HREF_IMPORTEXPORT = PREFIX_FOR_AHREF +IMPORT_EXPORT+ SUFIX_SHTML;
    public static final String LINK_BY_HREF_LOGOUT = PREFIX_FOR_AHREF +LOGOUT+ SUFIX_HTM;

    private static final String IMPORT_JSON_FILE= "test.txt";
    private static final String PATH_TO_JSON_FILES= "/home/mateusz/";

    public static final String PATH_TO_FILE_WITH_JSON_DATA = PATH_TO_JSON_FILES+IMPORT_JSON_FILE;
}

