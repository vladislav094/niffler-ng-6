package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import guru.qa.niffler.jupiter.extensions.CategoryExtension;
import guru.qa.niffler.jupiter.extensions.SpendingExtension;
import guru.qa.niffler.jupiter.extensions.UserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({
        UserExtension.class,
        ApiLoginExtension.class})
public @interface ApiLogin {
    String username() default "";
    String password() default "";

}
