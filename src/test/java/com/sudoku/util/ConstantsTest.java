package com.sudoku.util;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }

    @Test
    void testConstants() {
        assertThat(Constants.CMD_QUIT).isEqualTo("quit");
    }
}
