package org.main.fileio;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Unit test for simple App
public class FileOutTest extends TestCase {

    public FileOutTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( FileOutTest.class );
    }

//    public void testMainClass() {
//        assertTrue( true );
//    }
}