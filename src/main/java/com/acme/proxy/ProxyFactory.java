package com.acme.proxy;

import com.acme.annotation.Autowired;
import com.acme.enums.ProxyTypeEnum;
import com.acme.transantion.TransactionProxyCreate;

/**
 * @author ：wk
 * @date ：Created in 2022/10/7 4:36 下午
 * @description：代理工厂
 */
public class ProxyFactory {

    @Autowired
    private JdkTransactionProxy jdkTransactionProxy;

    @Autowired
    private CglibTransactionProxy cglibTransactionProxy;

    public TransactionProxyCreate getInstance(String type) {
        if (type.equals(ProxyTypeEnum.JDK.getType())) {
          return jdkTransactionProxy;
        }
        return cglibTransactionProxy;
    }
}
