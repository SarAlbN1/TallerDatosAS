package cliente.application.controllers;

import cliente.application.models.inventario.Item;
import cliente.application.services.InventoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para ProductController
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryServiceImpl inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        // Given
        Item item1 = Item.builder()
            .id(1L)
            .sku("PROD001")
            .nombre("Producto 1")
            .stock(10)
            .build();
        
        Item item2 = Item.builder()
            .id(2L)
            .sku("PROD002")
            .nombre("Producto 2")
            .stock(5)
            .build();

        when(inventoryService.getAllProducts()).thenReturn(Arrays.asList(item1, item2));

        // When & Then
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].sku").value("PROD001"))
            .andExpect(jsonPath("$[0].nombre").value("Producto 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].sku").value("PROD002"))
            .andExpect(jsonPath("$[1].nombre").value("Producto 2"));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        Item item = Item.builder()
            .id(1L)
            .sku("PROD001")
            .nombre("Producto 1")
            .stock(10)
            .build();

        when(inventoryService.getProductById(1L)).thenReturn(Optional.of(item));

        // When & Then
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.sku").value("PROD001"))
            .andExpect(jsonPath("$.nombre").value("Producto 1"));
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(inventoryService.getProductById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllCategories_ShouldReturnCategories() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").value("Electrónicos"))
            .andExpect(jsonPath("$[1]").value("Ropa"))
            .andExpect(jsonPath("$[2]").value("Hogar"));
    }
}
