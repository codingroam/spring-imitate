package com.acme.test.servlet;

import com.acme.test.service.AccountService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author ：wk
 * @date ：Created in 2022/10/31 10:45 上午
 * @description：
 */
@WebServlet(name = "accountServlet", urlPatterns = "/accountServlet")
public class AccountServlet extends HttpServlet {

    private AccountService accountService;

    public void setAccountService(AccountService accountService) {
        System.out.println("获取到XMl对象：");
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //设置请求体的字符编码
        req.setCharacterEncoding("UTF-8");

        String fromCardNo = req.getParameter("fromCardNo");
        String toCardNo = req.getParameter("toCardNo");
        BigDecimal money = new BigDecimal(req.getParameter("money"));
        System.out.printf("入参:fromCardNo:%s,toCardNo:%s,money:%f", fromCardNo, toCardNo, money);
//        Result result = new Result();
//        try {
//            accountService.trannsfer(fromCardNo, toCardNo, money);
//            result.setStatus("200");
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.setStatus("201");
//            result.setMessage(e.getMessage());
//        }
        //响应
        resp.setContentType("application/json;charset=utf-8");
//        resp.getWriter().println(JsonUtils.object2Json(result));

    }
}
