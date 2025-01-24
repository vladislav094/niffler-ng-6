package guru.qa.niffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface User {

    int friends() default 0;

    int outcomingRequests() default 0;

    int incomingRequests() default 0;

    String username() default "";

    Category[] categories() default {};

    Spending[] spendings() default {};
}
