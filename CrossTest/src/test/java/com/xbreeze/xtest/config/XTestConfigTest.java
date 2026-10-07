package com.xbreeze.xtest.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.xbreeze.xtest.exception.XTestException;

class XTestConfigTest {

    private static DatabaseConfig databaseConfig(String name) {
        DatabaseConfig config = new DatabaseConfig();
        config.setName(name);
        return config;
    }

    private static CompositeObjectConfig compositeObject(String name) {
        CompositeObjectConfig config = new CompositeObjectConfig();
        config.setName(name);
        return config;
    }

    @Test
    void uniqueNamesAreAccepted() throws XTestException {
        XTestConfig config = new XTestConfig();
        config.setDatabaseConfigs(new ArrayList<>(Arrays.asList(databaseConfig("source"), databaseConfig("target"))));
        assertDoesNotThrow(config::validateUniqueNames);
    }

    @Test
    void duplicateDatabaseConfigNameIsRejected() throws XTestException {
        XTestConfig config = new XTestConfig();
        config.setDatabaseConfigs(new ArrayList<>(Arrays.asList(databaseConfig("DWA_CONTROL"), databaseConfig("source"), databaseConfig("DWA_CONTROL"))));
        XTestException exception = assertThrows(XTestException.class, config::validateUniqueNames);
        assertEquals("The CrossTest config contains more than one DatabaseConfig named 'DWA_CONTROL' (names are not case sensitive). Each DatabaseConfig name must be unique.",
            exception.getMessage());
    }

    @Test
    void databaseConfigNamesDifferingOnlyInCaseAreRejected() throws XTestException {
        // getDatabaseConfig compares names case-insensitively, so these would shadow each other.
        XTestConfig config = new XTestConfig();
        config.setDatabaseConfigs(new ArrayList<>(Arrays.asList(databaseConfig("dwa_control"), databaseConfig("DWA_CONTROL"))));
        assertThrows(XTestException.class, config::validateUniqueNames);
    }

    @Test
    void compositeObjectNamesAreCaseSensitive() throws XTestException {
        // getCompositeObjectConfig compares names case-sensitively, so these are distinct objects.
        XTestConfig config = new XTestConfig();
        config.setCompositeObjects(new ArrayList<>(Arrays.asList(compositeObject("Customer"), compositeObject("customer"))));
        assertDoesNotThrow(config::validateUniqueNames);

        config.setCompositeObjects(new ArrayList<>(Arrays.asList(compositeObject("Customer"), compositeObject("Customer"))));
        assertThrows(XTestException.class, config::validateUniqueNames);
    }
}
