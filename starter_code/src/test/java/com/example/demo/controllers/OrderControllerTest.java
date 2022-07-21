package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private Cart cart;
    private User user;


    @Before
    public void init() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        cart = new Cart();
        cart.addItem(item);
        user = new User();
        user.setCart(cart);
    }

    @Test
    public void verify_submitOrder_happyPath() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void verify_submitOrder_unhappPath_userNofFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_getOrderForUser_happyPath() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(new UserOrder()));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void verify_getOrderForUser_unhappyPath_userNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertEquals(404, response.getStatusCodeValue());
    }
}
