package org.main.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Unit test for simple App
public class MessageTest extends TestCase {

    public MessageTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( MessageTest.class );
    }

//    public void testMainClass() {
//        assertTrue( true );
//    }
}