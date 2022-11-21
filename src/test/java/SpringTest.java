import com.acme.context.ClassPathXmlApplicationContext;
import com.acme.context.PropertiesApplicaitonContext;
import com.acme.test.controller.AccountController;
import com.acme.test.service.AccountService;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author ：wk
 * @date ：Created in 2022/10/1 6:14 下午
 * @description：
 */
public class SpringTest {

    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("beans.xml");
        AccountService accountService = classPathXmlApplicationContext.getBean(AccountService.class);
        String from = "6029621011001";
        String to = "6029621011000";
        BigDecimal money = new BigDecimal("100");
        accountService.trannsfer(from, to, money);
        System.out.println(accountService);
    }

    @Test
    public void testmvc() {
        PropertiesApplicaitonContext propertiesApplicaitonContext = new PropertiesApplicaitonContext("springmvc.properties");
        AccountController accountController = propertiesApplicaitonContext.getBean(AccountController.class);
        String name = "wk";
        String s = accountController.get(name);
        System.out.println("......");

    }
}
