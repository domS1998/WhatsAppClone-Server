package org.main.net;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Unit test for simple App
public class NetworkTest extends TestCase {

    public NetworkTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( NetworkTest.class );
    }

//    public void testMainClass() {
//        assertTrue( true );
//    }
}