package db.migration;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class V2__add_unique_id_to_customer implements JdbcMigration {

    private static String ADD_COLUMN_UNIQUE_ID = "alter table customer add uniqueid varchar(36)";
    private static String SELECT_ID_FROM_CUSTOMER = "select id from customer";
    private static String UPDATE_TABLE_STATEMENT = "update customer set uniqueid = ? where id = ?";

    @Override
    public void migrate(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute(ADD_COLUMN_UNIQUE_ID);
        }
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(SELECT_ID_FROM_CUSTOMER)) {
            while (rs.next()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TABLE_STATEMENT)) {
                    preparedStatement.setString(1, UUID.randomUUID().toString());
                    preparedStatement.setInt(2, rs.getInt("id"));
                    preparedStatement.execute();
                }
            }
        }
    }
}
