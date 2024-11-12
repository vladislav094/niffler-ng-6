package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UserdataDbClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class UsersClientExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(UsersClient.class)) {
                field.setAccessible(true);
                if ("db".equals(System.getProperty("users.client"))) {
                    field.set(testInstance, new UserdataDbClient());
                } else {
                    field.set(testInstance, new UsersApiClient());
                }
            }
        }
    }
}
