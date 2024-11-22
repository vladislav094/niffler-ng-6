package guru.qa.niffler.jupiter.extensions;

import com.codeborne.selenide.*;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private final List<SelenideDriver> drivers = new ArrayList<>();

    public List<SelenideDriver> drivers() {
        return drivers;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Configuration.browser = Browsers.CHROME;
        Configuration.browserSize = "19020x1080";
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        for (SelenideDriver driver : drivers) {
            if (driver.hasWebDriverStarted()) {
                driver.close();
            }
        }
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private void doScreenshot() {
        for (SelenideDriver driver : drivers) {
            if (driver.hasWebDriverStarted()) {
                Allure.addAttachment(
                        "Screen on fail for browser: " + driver.getSessionId(),
                        new ByteArrayInputStream(
                                ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                        )
                );
            }
        }
    }
}
