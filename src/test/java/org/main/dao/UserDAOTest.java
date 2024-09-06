package org.main.dao;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.main.exceptions.NoSuchUserException;
import org.main.exceptions.WrongPasswordException;

// Unit test for simple App
public class UserDAOTest extends TestCase {

    public UserDAOTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( UserDAOTest.class );
    }

    public void testLoadUser_0() {

        UserDAO dao = new UserDAO();
        try {
            dao.load("dan", "dan123");
            Assert.assertTrue(true);
        } catch (WrongPasswordException e) {
            Assert.assertTrue(false);
        } catch (NoSuchUserException e) {
            Assert.assertTrue(false);
        }
    }

    public void testLoadUser_1() {

        UserDAO dao = new UserDAO();
        try {
            dao.load("dan", "dan123_");
            Assert.assertTrue(false);
        } catch (WrongPasswordException e) {
            Assert.assertTrue(true);
        } catch (NoSuchUserException e) {
            Assert.assertTrue(false);
        }
        System.out.println(dao);
    }

    public void testLoadUser_2() {

        UserDAO dao = new UserDAO();
        try {
            dao.load("dan_", "dan123");
            Assert.assertTrue(false);
        } catch (WrongPasswordException e) {
            Assert.assertTrue(false);
        } catch (NoSuchUserException e) {
            Assert.assertTrue(true);
        }
    }







}