package org.scada_lts.factory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess.DatabaseType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;


public class DatabaseAwareBeanFactory<T> implements FactoryBean<T>, BeanNameAware, BeanFactoryAware

{

    private String beanName;
    private Class<?> type;
    private BeanFactory beanFactory;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.type = beanFactory.getType(getDaoBeanName());
    }

    @Override
    public T getObject() {
        return beanFactory.getBean(getDaoBeanName(), getObjectType());
    }

    @Override
    public Class<T> getObjectType() {
        return (Class<T>) type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getDaoBeanName() {
        String dbType = Common.getEnvironmentProfile().getString("db.type", "derby");
        String dbKey = DatabaseType.from(dbType).getKey();
        return beanName + "-" + dbKey;
    }
}
