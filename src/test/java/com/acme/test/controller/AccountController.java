package com.acme.test.controller;

import com.acme.annotation.Autowired;
import com.acme.mvc.annotation.Controller;
import com.acme.mvc.annotation.RequestMapping;
import com.acme.test.service.AccountService;

/**
 * @author ：wk
 * @date ：Created in 2022/10/13 7:46 上午
 * @description：
 */
@Controller
@RequestMapping("/wk/account")
public class AccountController {

    @Autowired
    private AccountService accountService;


    @RequestMapping("/get")
    public String get(String name) {
        return accountService.get(name);
    }
}
