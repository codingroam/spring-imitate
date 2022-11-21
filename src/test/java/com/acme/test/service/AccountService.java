package com.acme.test.service;

import java.math.BigDecimal;

/**
 * @author ：wk
 * @date ：Created in 2022/10/29 11:24 下午
 * @description：
 */
public interface AccountService {

    void trannsfer(String fromCardNo, String toCardNo, BigDecimal money) throws Exception;
    
    
    String get(String name);
}
