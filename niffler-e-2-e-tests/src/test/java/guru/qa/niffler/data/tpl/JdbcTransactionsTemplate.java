package guru.qa.niffler.data.tpl;

import guru.qa.niffler.jdbc.Connections;
import guru.qa.niffler.jdbc.JdbcConnectionHolder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

@ParametersAreNonnullByDefault
public class JdbcTransactionsTemplate {

    private final JdbcConnectionHolder holder;
    private final AtomicBoolean closeAfterActions = new AtomicBoolean(true);

    public JdbcTransactionsTemplate(String jdbcUrl) {
        this.holder = Connections.holder(jdbcUrl);
    }

    public JdbcTransactionsTemplate holdConnectionAfterAction() {
        this.closeAfterActions.set(false);
        return this;
    }

    public @Nullable <T> T execute(Supplier<T> action, int transactionIsolation) {
        Connection connection = null;
        try {
            connection = holder.connection();
            connection.setTransactionIsolation(transactionIsolation);
            connection.setAutoCommit(false);
            T result = action.get();
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (closeAfterActions.get()) {
                holder.close();
            }
        }
    }

    public <T> T execute(Supplier<T> action) {
        return execute(action, TRANSACTION_READ_COMMITTED);
    }

    public void execute(Runnable runnable, int transactionIsolation) {
        Connection connection = null;
        try {
            connection = holder.connection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(transactionIsolation);
            runnable.run();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public void execute(Runnable runnable) {
        execute(runnable, TRANSACTION_READ_COMMITTED);
    }
}
