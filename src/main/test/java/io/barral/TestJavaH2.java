package io.barral;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestJavaH2 {

    @Test
    public void testInsertAndPrint() {
        assertEquals(Integer.valueOf(10), JavaH2.insertAndPrint());
    }
}