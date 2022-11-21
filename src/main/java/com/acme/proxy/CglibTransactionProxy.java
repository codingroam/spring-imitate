package com.acme.proxy;

import com.acme.annotation.Autowired;
import com.acme.annotation.Component;
import com.acme.transantion.TransactionManager;
import com.acme.transantion.TransactionProxyCreate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author ：wk
 * @date ：Created in 2022/10/7 12:34 下午
 * @description：使用cglib动态代码生成对象
 */
@Component
public class CglibTransactionProxy implements TransactionProxyCreate {

    @Autowired
    private TransactionManager transactionManager;

    @Override
    public Object proxy(Object obj) {

        return Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try {
                    //开启事物
                    transactionManager.beginTransaction();
                    result = method.invoke(obj, objects);
                    transactionManager.comcommit();
                } catch (Exception e) {
                    e.printStackTrace();
                    transactionManager.rollback();
                    throw e;
                }
                return result;
            }
        });
    }
}
