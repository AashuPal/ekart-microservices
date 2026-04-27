package com.infy.ekart.cartservice.controller;

import com.infy.ekart.cartservice.dto.request.AddItemRequest;
import com.infy.ekart.cartservice.dto.request.UpdateItemQuantityRequest;
import com.infy.ekart.cartservice.dto.response.CartItemResponse;
import com.infy.ekart.cartservice.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts/{cartId}/items")
@Tag(name = "Cart Item Management", description = "APIs for managing cart items")
public class CartItemController {

    private final CartItemService cartItemService;

    // Manual constructor injection
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartItemResponse> addItem(
            @PathVariable String cartId,
            @Valid @RequestBody AddItemRequest request) {
        CartItemResponse response = cartItemService.addItem(cartId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all items in cart")
    public ResponseEntity<List<CartItemResponse>> getItems(@PathVariable String cartId) {
        List<CartItemResponse> items = cartItemService.getCartItems(cartId);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<CartItemResponse> updateItem(
            @PathVariable String cartId,
            @PathVariable String itemId,
            @Valid @RequestBody UpdateItemQuantityRequest request) {
        CartItemResponse response = cartItemService.updateQuantity(cartId, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeItem(
            @PathVariable String cartId,
            @PathVariable String itemId) {
        cartItemService.removeItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<Void> clearCart(@PathVariable String cartId) {
        cartItemService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}