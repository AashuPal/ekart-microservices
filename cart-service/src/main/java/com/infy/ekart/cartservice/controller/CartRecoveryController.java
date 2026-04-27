package com.infy.ekart.cartservice.controller;

import com.infy.ekart.cartservice.dto.response.CartResponse;
import com.infy.ekart.cartservice.service.AbandonedCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart Recovery", description = "APIs for abandoned cart recovery")
public class CartRecoveryController {

    private final AbandonedCartService abandonedCartService;

    // Manual constructor injection
    public CartRecoveryController(AbandonedCartService abandonedCartService) {
        this.abandonedCartService = abandonedCartService;
    }

    @PostMapping("/{cartId}/recover")
    @Operation(summary = "Recover an abandoned cart")
    public ResponseEntity<CartResponse> recoverCart(@PathVariable String cartId) {
        CartResponse response = abandonedCartService.recoverCart(cartId);
        return ResponseEntity.ok(response);
    }
}