package com.infy.ekart.cartservice.controller;

import com.infy.ekart.cartservice.dto.request.CheckoutRequest;
import com.infy.ekart.cartservice.dto.response.CheckoutResponse;
import com.infy.ekart.cartservice.service.CartCheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts/{cartId}/checkout")
@Tag(name = "Cart Checkout", description = "APIs for cart checkout")
public class CheckoutController {

    private final CartCheckoutService checkoutService;

    // Manual constructor injection
    public CheckoutController(CartCheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    @Operation(summary = "Checkout cart and create order")
    public ResponseEntity<CheckoutResponse> checkout(
            @PathVariable String cartId,
            @Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.checkout(cartId, request);
        return ResponseEntity.ok(response);
    }
}