package guru.qa.niffler.jupiter.extensions;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import guru.qa.niffler.utils.SelenideUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;

public class NonStaticBrowserExtension implements
        BeforeEachCallback,
        BeforeAllCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private static final ThreadLocal<SelenideDriver> driverThreadLocal =
            ThreadLocal.withInitial(() -> new SelenideDriver(SelenideUtils.chromeConfig));

    public static SelenideDriver getDriver() {
        return driverThreadLocal.get();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        driverThreadLocal.set(new SelenideDriver(SelenideUtils.chromeConfig));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        SelenideDriver driver = getDriver();
        if (driver !=null && driver.hasWebDriverStarted()) {
            driver.close();
        }
        driverThreadLocal.remove();
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
        SelenideDriver driver = getDriver();
        if (driver.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail for browser: " + driver.getSessionId(),
                    new ByteArrayInputStream(
                            ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
        driverThreadLocal.remove();
    }
}
