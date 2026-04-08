package org.scada_lts.factory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess.DatabaseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DatabaseAwareBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(DatabaseAwareBeanFactory.class);
    private static final String DEFAULT_DB_TYPE = "mysql";
    private static final String DB_PLUGIN_CONFIG_PATTERN = "classpath*:META-INF/scadalts-db/%s.xml";
    private static final Object DB_PLUGIN_LOAD_MONITOR = new Object();
    private static final Set<String> DB_PLUGIN_LOAD_ATTEMPTS = Collections.synchronizedSet(new HashSet<String>());

    private ApplicationContext applicationContext;
    private String prefix;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public T getObject() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is not set for DatabaseAwareBeanFactory.");
        }
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalStateException("Bean prefix is not set for DatabaseAwareBeanFactory.");
        }

        String dbKey = resolveDbKey();
        String targetBeanName = buildBeanName(dbKey);
        if (applicationContext.containsBean(targetBeanName)) {
            return getBean(targetBeanName);
        }

        ensureDbPluginLoaded(dbKey);
        if (applicationContext.containsBean(targetBeanName)) {
            return getBean(targetBeanName);
        }

        String msg = "DB plugin not found for db.type=" + dbKey + ". Expected bean '" + targetBeanName
                + "'. Verify DB plugin JAR and META-INF/scadalts-db/" + dbKey + ".xml are present on classpath.";
        LOG.error(msg);
        throw new IllegalStateException(msg);
    }

    @Override
    public Class<?> getObjectType() {
        if (applicationContext == null || prefix == null || prefix.trim().isEmpty()) {
            return Object.class;
        }
        String dbKey = resolveDbKey();
        Class<?> type = applicationContext.getType(buildBeanName(dbKey));
        return type == null ? Object.class : type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private String resolveDbKey() {
        String dbType = Common.getEnvironmentProfile().getString("db.type", DEFAULT_DB_TYPE);
        return DatabaseType.from(dbType).getKey();
    }

    private void ensureDbPluginLoaded(String dbKey) {
        if (!(applicationContext instanceof ConfigurableApplicationContext)) {
            return;
        }

        String databaseAccessBeanName = "databaseAccess-" + dbKey;
        if (applicationContext.containsBean(databaseAccessBeanName)) {
            return;
        }

        synchronized (DB_PLUGIN_LOAD_MONITOR) {
            if (applicationContext.containsBean(databaseAccessBeanName)) {
                return;
            }
            if (DB_PLUGIN_LOAD_ATTEMPTS.contains(dbKey)) {
                return;
            }
            DB_PLUGIN_LOAD_ATTEMPTS.add(dbKey);

            ConfigurableApplicationContext configurable = (ConfigurableApplicationContext) applicationContext;
            ConfigurableListableBeanFactory beanFactory = configurable.getBeanFactory();
            if (!(beanFactory instanceof BeanDefinitionRegistry)) {
                return;
            }

            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);
            String pattern = String.format(DB_PLUGIN_CONFIG_PATTERN, dbKey);
            int loaded = reader.loadBeanDefinitions(pattern);
            if (loaded > 0) {
                LOG.info("Loaded " + loaded + " DB plugin bean definition(s) from " + pattern);
            }
        }
    }

    private String buildBeanName(String dbKey) {
        return prefix + "-" + dbKey;
    }

    @SuppressWarnings("unchecked")
    private T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }
}
