package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.service.ThreadSafeCookieStore;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CookieJarExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ThreadSafeCookieStore.INSTANCE.removeAll();
    }
}
