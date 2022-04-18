package com.example.myapplication;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.view.RegisterUser;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestRegisterUser {

    @Test
    public void testCheckCredentialsEmptyUsername() {
        assertFalse(RegisterUser.checkCredentials("test@student.tue.nl", "", "123456", null, null, null));
    }

    @Test
    public void testCheckCredentialsNonTUEMail() {
        assertFalse(RegisterUser.checkCredentials("test@hotmail.com", "testUser", "123456", null, null, null));
    }

    @Test
    public void testCheckCredentialsShortPassword() {
        assertFalse(RegisterUser.checkCredentials("test@hotmail.com", "testUser", "12345", null, null, null));
    }

    @Test
    public void testCheckCredentialsNonEmail() {
        assertFalse(RegisterUser.checkCredentials("test.hotmail.com", "testUser", "123456", null, null, null));
    }

    @Test
    public void testCheckCredentialsValidCredentials() {
        assertTrue(RegisterUser.checkCredentials("test@student.tue.nl", "testUser", "123456", null, null, null));
        assertTrue(RegisterUser.checkCredentials("test@tue.nl", "testUser69", "123456$^&EAadwa123€", null, null, null));
        assertTrue(RegisterUser.checkCredentials("test@tue.nl", "testUser69-=123€", "123456$^&EAadwa123€", null, null, null));
    }
}