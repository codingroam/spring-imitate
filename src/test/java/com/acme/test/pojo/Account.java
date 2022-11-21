package com.acme.test.pojo;

import java.math.BigDecimal;

/**
 * @author ：wk
 * @date ：Created in 2022/10/29 11:40 下午
 * @description：
 */
public class Account {

    private Long id;

    private String name;

    private String cardNo;

    private BigDecimal money;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
