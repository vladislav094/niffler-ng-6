package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UserdataDbClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserExtension implements ParameterResolver, BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = "12345";

    private final UsersClient usersClient = new UserdataDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    if ("".equals(anno.username())) {
                        final String username = RandomDataUtils.randomUsername();
                        UdUserJson testUser = usersClient.createUser(username, defaultPassword);
                        usersClient.createOutcomingInvitation(testUser, anno.outcomingRequests());
                        usersClient.createIncomingInvitation(testUser, anno.incomingRequests());
                        usersClient.createFriend(testUser, anno.friends());

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                testUser
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UdUserJson.class);
    }

    @Override
    public UdUserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UdUserJson.class);
    }
}
