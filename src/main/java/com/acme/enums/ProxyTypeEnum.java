package com.acme.enums;

/**
 * @author ：wk
 * @date ：Created in 2022/10/7 4:42 下午
 * @description：代理类型
 */
public enum ProxyTypeEnum {
    JDK("jdk", "jdk动态代理"),
    CGLIB("cglib", "cglib动态代理");

    private String type;

    private String value;

    ProxyTypeEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
