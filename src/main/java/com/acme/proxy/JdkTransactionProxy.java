package com.acme.proxy;

import com.acme.annotation.Autowired;
import com.acme.annotation.Component;
import com.acme.transantion.TransactionManager;
import com.acme.transantion.TransactionProxyCreate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author ：wk
 * @date ：Created in 2022/10/7 12:33 下午
 * @description：使用jdk动态代码管理事物
 */
@Component
public class JdkTransactionProxy implements TransactionProxyCreate {

    @Autowired
    private TransactionManager transactionManager;

    @Override
    public Object proxy(Object obj) {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                try {
                    //开启事物
                    transactionManager.beginTransaction();
                    result= method.invoke(obj, args);
                    //提交事物
                    transactionManager.comcommit();
                } catch (Exception e) {
                    e.printStackTrace();
                    //回滚事物
                    transactionManager.rollback();
                    throw e;
                }
                return result;
            }
        });
    }
}
