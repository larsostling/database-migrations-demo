package com.larsostling.liquibasedemo.liquibase;

import liquibase.change.custom.CustomTaskChange;
import liquibase.change.custom.CustomTaskRollback;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AddUniqueIdToCustomer implements CustomTaskChange, CustomTaskRollback {

    private static String ADD_COLUMN_UNIQUE_ID = "alter table customer add uniqueid varchar(36)";
    private static String SELECT_ID_FROM_CUSTOMER = "select id from customer";
    private static String UPDATE_TABLE_STATEMENT = "update customer set uniqueid = ? where id = ?";

    private static String DROP_COLUMN_UNIQUE_ID = "alter table customer drop uniqueid";

    @Override
    public void execute(Database database) throws CustomChangeException {
        Connection connection = ((JdbcConnection) database.getConnection()).getUnderlyingConnection();
        try {
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
        } catch (SQLException e) {
            throw new CustomChangeException("SQL error adding unique id to customer", e);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

    @Override
    public void rollback(Database database) throws CustomChangeException, RollbackImpossibleException {
        Connection connection = ((JdbcConnection) database.getConnection()).getUnderlyingConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute(DROP_COLUMN_UNIQUE_ID);
        } catch (Exception e) {
            throw new CustomChangeException(e);
        }
    }
}
