package com.acme.jdbc;

/**
 * @author ：wk
 * @date ：Created in 2022/10/7 11:37 上午
 * @description：数据源配置
 */
public class DatasourceProperty {

    private String url;

    private String username;

    private String driverClassName;

    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
