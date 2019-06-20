package org.scada_lts.serorepl.utils;

import com.serotonin.ShouldNeverHappenException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertiesUtils {

    private static final String CANT_PARSE_BOOLEAN = "Can't parse boolean from properties key: ";
    private final ResourceBundle props;
    private static final Log LOG = LogFactory.getLog(org.scada_lts.serorepl.utils.PropertiesUtils.class);


    public PropertiesUtils(String bundleName) {
        this(bundleName, false);
    }

    public PropertiesUtils(String bundleName, boolean allowMissing){
        Object resourceBundle;
        try{
            resourceBundle = ResourceBundle.getBundle(bundleName);
        }catch (MissingResourceException e){
            if (!allowMissing) throw e;

            try{
                resourceBundle = new PropertyResourceBundle(new StringReader("")); {
                }
            }catch (IOException e1){
                throw new ShouldNeverHappenException(e1);
            }
        }
        props = (ResourceBundle)resourceBundle;
    }

    public String getString(String key){
        if (props == null) {
            throw new MissingResourceException("", "", key);
        } else {
            return props.getString(key);
        }
    }

    public String getString(String key, String defaultValue){
        try {
            String value = getString(key);
            return value.trim().length() == 0 ? defaultValue : value;
        } catch (MissingResourceException var4) {
            return defaultValue;
        }
    }

    public int getInt(String key){
        if (props == null) {
            throw new MissingResourceException("", "", key);
        } else {
            return Integer.parseInt(props.getString(key));
        }
    }

    public int getInt(String key, int defaultValue){

        if (props == null){
            return defaultValue;
        }else{
            try {
                String value = props.getString(key);
                if(value.trim().length() == 0){
                    return defaultValue;
                }
                return Integer.parseInt(value);
            }catch (MissingResourceException | NumberFormatException e){

            }
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {

        if (props == null){
            throw new MissingResourceException("","", key);
        }else{

            try{
                String value = props.getString(key);
                if (value.trim().length() == 0) return defaultValue;
                if (value.equals("true"))return true;
                if (value.equals("false"))return false;
                LOG.warn(CANT_PARSE_BOOLEAN + key + ", value=" + value);

            }
            catch (MissingResourceException e){

            }

        }
        return defaultValue;
    }

}
