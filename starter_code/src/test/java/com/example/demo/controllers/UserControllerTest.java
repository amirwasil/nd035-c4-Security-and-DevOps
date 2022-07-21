package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void init() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void verify_createUser_happyPath() {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashedPassword");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void verify_createUser_unhappyPath_anyBlank() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void verify_createUser_unhappyPath_anyNull() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void verify_createUser_unhappyPath_invalidConfirmPassword() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("diffPassword");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void verify_createUser_unhappyPath_invalidPasswordLength() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void verify_createUser_unhappyPath_usernameExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(new User());
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

}
