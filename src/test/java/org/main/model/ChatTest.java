package org.main.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Unit test for simple App
public class ChatTest extends TestCase {

    public ChatTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( ChatTest.class );
    }

//    public void testMainClass() {
//        assertTrue( true );
//    }
}