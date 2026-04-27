package com.infy.ekart.cartservice.controller;

import com.infy.ekart.cartservice.dto.request.CreateCartRequest;
import com.infy.ekart.cartservice.dto.request.MergeCartRequest;
import com.infy.ekart.cartservice.dto.response.CartResponse;
import com.infy.ekart.cartservice.dto.response.ErrorResponse;
import com.infy.ekart.cartservice.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart Management", description = "APIs for managing shopping carts")
public class CartController {

    private final CartService cartService;

    // Manual constructor injection
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cart created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CartResponse> createCart(@Valid @RequestBody CreateCartRequest request) {
        CartResponse response = cartService.createCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Get cart by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart found"),
        @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseEntity<CartResponse> getCart(
            @Parameter(description = "Cart ID") @PathVariable String cartId) {
        CartResponse response = cartService.getCartById(cartId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cartId}/status")
    @Operation(summary = "Update cart status")
    public ResponseEntity<CartResponse> updateCartStatus(
            @PathVariable String cartId,
            @RequestParam String status) {
        CartResponse response = cartService.updateCartStatus(cartId, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a cart")
    public ResponseEntity<Void> deleteCart(
            @Parameter(description = "Cart ID") @PathVariable String cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest cart with user cart")
    public ResponseEntity<CartResponse> mergeCarts(@Valid @RequestBody MergeCartRequest request) {
        CartResponse response = cartService.mergeCarts(request);
        return ResponseEntity.ok(response);
    }
}