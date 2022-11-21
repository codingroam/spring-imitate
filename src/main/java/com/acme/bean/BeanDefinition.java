package com.acme.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ：wk
 * @date ：Created in 2022/10/5 8:33 下午
 * @description： bean定义
 */
public class BeanDefinition {

    private String id;

    private String className;

    private Class<?> beanClass;

    private List<PropertyArg> propertyArgList = new LinkedList<>();

    private Scope scope = Scope.SINGLETON;

    private boolean enableTransaction = false;

    private boolean lazyInit = false;

    public static enum Scope {
        /**
         * 作用域 单例，多例（原型）
         */
        SINGLETON,
        PROTOT
    }

    public boolean isSingle() {
        return Scope.SINGLETON.equals(scope);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public List<PropertyArg> getPropertyArgList() {
        return propertyArgList;
    }

    public void setPropertyArgList(List<PropertyArg> propertyArgList) {
        this.propertyArgList = propertyArgList;
    }

    public boolean isEnableTransaction() {
        return enableTransaction;
    }

    public void setEnableTransaction(boolean enableTransaction) {
        this.enableTransaction = enableTransaction;
    }

    public static class PropertyArg {
        private Class<?> type;
        private String name;
        private String ref;
        private String value;
        private String parentId;

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

    }
}
