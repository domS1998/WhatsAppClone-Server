package org.main.dao;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Unit test for simple App
public class ChatDAOTest extends TestCase {

    public ChatDAOTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( ChatDAOTest.class );
    }

//    public void testMainClass() {
//        assertTrue( true );
//    }
}