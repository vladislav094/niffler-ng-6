package guru.qa.niffler.test.debug;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotations.ParallelBrowser;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.test.web.BaseWebTest;
import guru.qa.niffler.utils.BrowserConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

@ParallelBrowser
public class ParallelBrowserTest extends BaseWebTest {

    @ParameterizedTest
    @EnumSource(BrowserConverter.Browser.class)
    void testParallelBrowserLogin(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .fillLoginPage("dell", "gateway")
                .submit(new LoginPage(driver));
    }
}
