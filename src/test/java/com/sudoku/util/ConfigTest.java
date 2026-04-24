package com.sudoku.util;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigTest {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<Config> constructor = Config.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }

    @Test
    void testConfigValues() {
        assertThat(Config.SUBGRID_ROWS).isBetween(1, Config.MAX_SUBGRID_SIZE);
        assertThat(Config.SUBGRID_COLS).isBetween(1, Config.MAX_SUBGRID_SIZE);
        assertThat(Config.SIZE).isEqualTo(Config.SUBGRID_ROWS * Config.SUBGRID_COLS);
    }

    @Test
    void testParseAndValidateInvalid() throws Exception {
        Method method = Config.class.getDeclaredMethod("parseAndValidate", String.class);
        method.setAccessible(true);

        // Use a key that exists in the bundle but override with invalid system property
        String key = "subgrid.rows";
        String originalValue = System.getProperty(key);
        
        try {
            System.setProperty(key, "0");
            assertThatThrownBy(() -> {
                try {
                    method.invoke(null, key);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Invalid value for subgrid.rows");

            System.setProperty(key, String.valueOf(Config.MAX_SUBGRID_SIZE + 1));
            assertThatThrownBy(() -> {
                try {
                    method.invoke(null, key);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Invalid value for subgrid.rows");
        } finally {
            if (originalValue != null) {
                System.setProperty(key, originalValue);
            } else {
                System.clearProperty(key);
            }
        }
    }
}
