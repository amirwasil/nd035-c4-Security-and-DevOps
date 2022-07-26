package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private User user = mock(User.class);
    private Item item;
    private Cart cart;

    @Before
    public void init() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        cart = new Cart();
    }

    @Test
    public void verify_addToCart_happyPath() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        when(user.getCart()).thenReturn(cart);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        cart = response.getBody();
        Assertions.assertThat(cart.getItems()).isNotEmpty();
    }

    @Test
    public void verify_removeFromCart_happyPath() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        cart.addItem(item);
        when(user.getCart()).thenReturn(cart);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        cart = response.getBody();
        Assertions.assertThat(cart.getItems()).isEmpty();
    }

    @Test
    public void verify_addToCart_unhappyPath_userNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_removeFromCat_unhappyPath_userNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_addToCart_unhappyPath_itemNotFound() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(anyObject())).thenReturn(Optional.empty());
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());

    }

    @Test
    public void verify_removeFromCat_unhappyPath_itemNotFound() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(anyObject())).thenReturn(Optional.empty());
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());

    }
}
