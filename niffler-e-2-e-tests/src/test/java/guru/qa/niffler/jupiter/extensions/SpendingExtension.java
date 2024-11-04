package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingExtension implements ParameterResolver, BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendClient spendClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
                        List<SpendJson> result = new ArrayList<>();

                        UdUserJson user = context.getStore(UserExtension.NAMESPACE)
                                .get(context.getUniqueId(), UdUserJson.class);

                        for (Spending spendAnno : userAnno.spendings()) {
                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),
                                    new CategoryJson(
                                            null,
                                            spendAnno.category(),
                                            user != null ? user.username() : userAnno.username(),
                                            false
                                    ),
                                    CurrencyValues.RUB,
                                    spendAnno.amount(),
                                    spendAnno.description(),
                                    user != null ? user.username() : userAnno.username()
                            );

                            SpendJson createdSpend = spendClient.createSpend(spend);
                            result.add(createdSpend);

                            if (user != null) {
                                user.testData().spendings().addAll(result);
                            } else {
                                context.getStore(NAMESPACE).put(
                                        context.getUniqueId(),
                                        result
                                );
                            }
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(SpendJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (SpendJson[]) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class)
                .toArray();
    }
}
