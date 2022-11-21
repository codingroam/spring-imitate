package com.acme.test.service.Impl;

import com.acme.annotation.Autowired;
import com.acme.annotation.Service;
import com.acme.annotation.Transactional;
import com.acme.test.dao.AccountDao;
import com.acme.test.pojo.Account;
import com.acme.test.service.AccountService;

import java.math.BigDecimal;

/**
 * @author ：wk
 * @date ：Created in 2022/10/29 11:25 下午
 * @description：
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;


    @Override
    public void trannsfer(String fromCardNo, String toCardNo, BigDecimal money) throws Exception {
        Account from = accountDao.queryByCardNo(fromCardNo);
        Account to = accountDao.queryByCardNo(toCardNo);

        from.setMoney(from.getMoney().subtract(money));
        to.setMoney(to.getMoney().add(money));

        accountDao.updateAccountByCardNo(from);
//        int i = 1/0;
        accountDao.updateAccountByCardNo(to);
    }

    @Override
    public String get(String name) {
        System.out.println("get:" + name);
        return name;
    }
}
