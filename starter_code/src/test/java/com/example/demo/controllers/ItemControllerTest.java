package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private List<Item> items;

    @Before
    public void init() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
        items = new ArrayList<>();
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        items.add(item);
    }

    @Test
    public void verify_getItems() {
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        List<Item> items = response.getBody();
        Assertions.assertThat(items).isNotEmpty();
    }

    @Test
    public void verify_getItemById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(items.get(0)));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        Item item = response.getBody();
        assertNotNull(item);
    }

    @Test
    public void verify_getItemByName() {
        String itemName = "Round Widget";
        when(itemRepository.findByName(itemName)).thenReturn(items);
        ResponseEntity<List<Item>> response  = itemController.getItemsByName(itemName);
        assertNotNull(response);
        List<Item> items = response.getBody();
        Assertions.assertThat(items).isNotEmpty();
    }
}
