package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.jdbc.Connections;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
        EntityManagers.closeAllEmfs();
    }
}
