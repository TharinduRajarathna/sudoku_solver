package com.sudoku.util;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import static org.assertj.core.api.Assertions.assertThat;

class MessagesTest {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<Messages> constructor = Messages.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }

    @Test
    void testGetMessage() {
        String msg = Messages.get("msg.welcome");
        assertThat(msg).contains("Welcome to Sudoku");
    }

    @Test
    void testGetFormattedMessage() {
        String msg = Messages.get("msg.hint", "A1", 5);
        assertThat(msg).contains("A1").contains("5");
    }
}
