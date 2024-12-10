package guru.qa.niffler.jupiter.annotations.meta;

import guru.qa.niffler.jupiter.extensions.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        BrowserExtension.class,
        AllureJunit5.class,
        UserExtension.class,
        CategoryExtension.class,
        SpendingExtension.class,
        ApiLoginExtension.class,
        UserQueueExtension.class,
        ClearEnvExtension.class})
public @interface WebTest {
}