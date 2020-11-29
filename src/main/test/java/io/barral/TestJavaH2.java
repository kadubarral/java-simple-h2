package io.barral;

import org.junit.Test;

import java.net.UnknownHostException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestJavaH2 {

    @Test
    public void testInsertAndPrint() throws InterruptedException, SQLException, UnknownHostException {
        assertEquals(Integer.valueOf(10), JavaH2.insertAndPrint());
    }
}