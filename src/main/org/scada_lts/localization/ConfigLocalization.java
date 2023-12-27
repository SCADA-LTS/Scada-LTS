package org.scada_lts.localization;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @project Scada-LTS-master
 * @autor grzegorz.bylica@gmail.com on 11.10.18
 */
public class ConfigLocalization {

    private static ConfigLocalization ourInstance = new ConfigLocalization();
    private ReloadableResourceBundleMessageSource messageSource;

    public static ConfigLocalization getInstance() {
        return ourInstance;
    }

    private ConfigLocalization() {
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
    }

    public MessageSource messageSource () {
        return messageSource;
    }
}
