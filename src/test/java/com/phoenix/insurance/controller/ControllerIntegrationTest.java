package com.phoenix.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.insurance.dto.UserRegistrationDto;
import com.phoenix.insurance.model.Product;
import com.phoenix.insurance.service.ProductService;
import com.phoenix.insurance.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({UserController.class, ProductController.class})
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/users/register - Success")
    void testRegisterUser_Success() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("123", "0501112222", "test@test.com");

        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(100L);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("קוד הסשן הוא 100")));
    }

    @Test
    @DisplayName("POST /api/users/register - Missing ID (400 Bad Request)")
    void testRegisterUser_BadRequest() throws Exception {
        UserRegistrationDto invalidDto = new UserRegistrationDto("", "050", "test@test.com");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/update - Success with Header")
    void testUpdateProduct_Success() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("ביטוח רכב");
        product.setPrice(new BigDecimal("1500.0"));

        mockMvc.perform(put("/api/products/update")
                        .header("X-OTP-Session-Id", 555L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully"));
    }

    @Test
    @DisplayName("POST /api/users/{id}/purchase/{productId} - Verify Header and PathVariable")
    void testPurchaseProduct() throws Exception {
        mockMvc.perform(post("/api/users/123/purchase/10")
                        .header("X-OTP-Session-Id", 999L))
                .andExpect(status().isOk())
                .andExpect(content().string("Product purchased successfully!"));
    }
}