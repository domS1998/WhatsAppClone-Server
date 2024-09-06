package org.main.database.dao;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Unit test for simple App
public class UserTest extends TestCase {

    public UserTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( UserTest.class );
    }

//    public void testMainClass() {
//        assertTrue( true );
//    }
}