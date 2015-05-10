package com.larsostling.liquibasedemo;

import liquibase.Contexts;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.Liquibase;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple demo for running liquibase from java code.
 */
public class LiquibaseDemo {

    public static void main(String[] args) throws Exception {
        Connection connection = null;
        Liquibase liquibase = null;
        try {
            connection = createConnection();
            liquibase = new Liquibase("liquibase/changelog.xml",
                    new ClassLoaderResourceAccessor(),
                    createDataBase(connection));
            liquibase.update((Contexts) null);
        } finally {
            if (liquibase != null) {
                liquibase.forceReleaseLocks();
            }
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
    }

    private static Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:file:target/liquibasedemo", "sa", null);
    }

    private static Database createDataBase(Connection connection) throws DatabaseException {
        DatabaseConnection liquibaseConnection = new JdbcConnection(connection);
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(liquibaseConnection);
    }

}
