package com.serotonin.mango.util;

import org.scada_lts.dao.SystemSettingsDAO;

public class SendEmailConfig {

    private final String fromAddr;
    private final String pretty;
    private final String host;
    private final int port;
    private final boolean authorization;
    private final String username;
    private final String password;
    private final boolean tls;

    public SendEmailConfig(String fromAddr, String pretty, String host, int port, boolean authorization,
                           String username, String password, boolean tls) {
        this.fromAddr = fromAddr;
        this.pretty = pretty;
        this.host = host;
        this.port = port;
        this.authorization = authorization;
        this.username = username;
        this.password = password;
        this.tls = tls;
    }

    public static SendEmailConfig newConfigFromSystemSettings() throws Exception {

        String fromAddr = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_ADDRESS);
        String pretty = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_NAME);
        String host = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_HOST);
        int port = SystemSettingsDAO.getIntValue(SystemSettingsDAO.EMAIL_SMTP_PORT);
        boolean authorization = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_AUTHORIZATION);
        String username = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_USERNAME);
        String password = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_PASSWORD);
        boolean tls = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_TLS);

        validateConfig(SystemSettingsDAO.EMAIL_FROM_ADDRESS, fromAddr);
        validateConfig(SystemSettingsDAO.EMAIL_SMTP_HOST, host);
        if(authorization) {
            validateAuthorizationConfig(username, password);
        }
        return new SendEmailConfig(fromAddr, pretty, host, port, authorization, username, password, tls);
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public String getPretty() {
        return pretty;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isAuthorization() {
        return authorization;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isTls() {
        return tls;
    }

    public static void validateSystemSettings() throws Exception {
        String fromAddr = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_ADDRESS);
        String host = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_HOST);
        boolean authorization = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_AUTHORIZATION);
        String username = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_USERNAME);
        String password = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_PASSWORD);

        validateConfig(SystemSettingsDAO.EMAIL_FROM_ADDRESS, fromAddr);
        validateConfig(SystemSettingsDAO.EMAIL_SMTP_HOST, host);
        if(authorization) {
            validateAuthorizationConfig(username, password);
        }
    }

    private static void validateConfig(String name, Object object) throws Exception {
        if(object == null)
            throw new IllegalStateException("Email config properties: " + name + " is null!");
        if((object instanceof String) && ((String)object).isEmpty())
            throw new IllegalStateException("Email config properties: " + name + " is empty!");
    }

    private static void validateAuthorizationConfig(String user, String password) throws Exception {
        if((user == null || user.isEmpty()) && (password == null || password.isEmpty()))
            throw new IllegalStateException("Email config properties: " + SystemSettingsDAO.EMAIL_SMTP_USERNAME + " / " + SystemSettingsDAO.EMAIL_SMTP_PASSWORD + " is null!");
    }
}
