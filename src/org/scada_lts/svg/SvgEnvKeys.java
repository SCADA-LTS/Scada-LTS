package org.scada_lts.svg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SvgEnvKeys {
    SVG_ONLY_XML_KEY("svg.validator.only-xml"),
    SVG_VALIDATOR_ENABLED_KEY("svg.validator.enabled"),
    SVG_SCHEMA_PATH_KEY("svg.validator.schemas"),
    SVG_IGNORE_TAGS_KEY("svg.validator.tags.ignore"),
    SVG_IGNORE_MESSAGES_KEY("svg.validator.messages.ignore");

    private static final Log LOG = LogFactory.getLog(SvgEnvKeys.class);

    private final String key;

    SvgEnvKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static boolean isOnlyXml() {
        try {
            return ScadaConfig.getInstance().getBoolean(SVG_ONLY_XML_KEY.getKey(), false);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return false;
        }
    }

    public static boolean isEnabled() {
        try {
            return ScadaConfig.getInstance().getBoolean(SVG_VALIDATOR_ENABLED_KEY.getKey(), true);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return true;
        }
    }

    public static String[] getSchemaPaths() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(SVG_SCHEMA_PATH_KEY.getKey(), "").split(";");
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return new String[0];
        }
    }

    public static String[] getIgnoreTags() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(SVG_IGNORE_TAGS_KEY.getKey(), "").split(";");
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return new String[0];
        }
    }

    public static String[] getIgnoreMsgs() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(SVG_IGNORE_MESSAGES_KEY.getKey(), "").split(";");
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return new String[0];
        }
    }

    private static String get(String key) {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(key, "");
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return "";
        }
    }

    public static Map<String, String> getConfig() {
        return Stream.of(SvgEnvKeys.values()).collect(Collectors.toMap(SvgEnvKeys::getKey, a -> get(a.getKey())));
    }
}
