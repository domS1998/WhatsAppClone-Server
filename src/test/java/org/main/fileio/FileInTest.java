package org.main.fileio;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Unit test for simple App
public class FileInTest extends TestCase {

    public FileInTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( FileInTest.class );
    }

//    public void testMainClass() {
//        assertTrue( true );
//    }
}