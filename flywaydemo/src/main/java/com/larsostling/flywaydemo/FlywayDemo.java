package com.larsostling.flywaydemo;

import org.flywaydb.core.Flyway;

/**
 * Simple demo for running flyway from java code.
 */
public class FlywayDemo {

    public static void main( String[] args ) {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:h2:file:target/demodb", "sa", null);
        flyway.migrate();
    }

}
