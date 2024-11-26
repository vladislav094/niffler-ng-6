package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements ParameterResolver, BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final SpendClient spendClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.categories())) {
                        List<CategoryJson> result = new ArrayList<>();
                        for (Category categoryAnno : userAnno.categories()) {
                            final String categoryName = "".equals(categoryAnno.name())
                                    ? randomCategoryName()
                                    : categoryAnno.name();

                            UdUserJson user = context.getStore(UserExtension.NAMESPACE)
                                    .get(context.getUniqueId(), UdUserJson.class);

                            CategoryJson category = new CategoryJson(
                                    null,
                                    categoryName,
                                    user != null ? user.username() : userAnno.username(),
                                    false
                            );
                            CategoryJson createdCategory = spendClient.createCategory(category);

                            if (categoryAnno.archived()) {
                                createdCategory = spendClient.updateCategory(new CategoryJson(createdCategory.id(),
                                        createdCategory.name(), createdCategory.username(), true));
                            }

                            result.add(createdCategory);
                            if (user != null) {
                                user.testData().categories().addAll(result);
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
                .isAssignableFrom(CategoryJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (CategoryJson[]) extensionContext.getStore(CategoryExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class)
                .toArray();
    }
}
