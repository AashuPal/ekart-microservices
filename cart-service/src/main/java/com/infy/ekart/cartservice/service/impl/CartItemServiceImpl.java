package com.infy.ekart.cartservice.service.impl;

import com.infy.ekart.cartservice.client.InventoryServiceClient;
import com.infy.ekart.cartservice.client.PricingServiceClient;
import com.infy.ekart.cartservice.dto.request.AddItemRequest;
import com.infy.ekart.cartservice.dto.request.UpdateItemQuantityRequest;
import com.infy.ekart.cartservice.dto.response.CartItemResponse;
import com.infy.ekart.cartservice.entity.Cart;
import com.infy.ekart.cartservice.entity.CartItem;
import com.infy.ekart.cartservice.exception.InsufficientStockException;
import com.infy.ekart.cartservice.exception.ItemNotFoundException;
import com.infy.ekart.cartservice.mapper.CartItemMapper;
import com.infy.ekart.cartservice.repository.CartItemRepository;
import com.infy.ekart.cartservice.repository.CartRepository;
import com.infy.ekart.cartservice.service.CartItemService;
import com.infy.ekart.cartservice.validator.CartValidator;
import com.infy.ekart.cartservice.validator.ItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private static final Logger log = LoggerFactory.getLogger(CartItemServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartValidator cartValidator;
    private final ItemValidator itemValidator;
    private final CartItemMapper cartItemMapper;
    private final InventoryServiceClient inventoryClient;
    private final PricingServiceClient pricingClient;

    public CartItemServiceImpl(CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               CartValidator cartValidator,
                               ItemValidator itemValidator,
                               CartItemMapper cartItemMapper,
                               InventoryServiceClient inventoryClient,
                               PricingServiceClient pricingClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartValidator = cartValidator;
        this.itemValidator = itemValidator;
        this.cartItemMapper = cartItemMapper;
        this.inventoryClient = inventoryClient;
        this.pricingClient = pricingClient;
    }

    @Override
    public CartItemResponse addItem(String cartId, AddItemRequest request) {
        log.info("Adding item to cart {}: product={}, qty={}", cartId, request.productId(), request.quantity());

        UUID cartUuid = UUID.fromString(cartId);
        Cart cart = cartValidator.validateAndGetActiveCart(cartUuid);
        
        itemValidator.validateQuantity(request.quantity());

        boolean stockAvailable = inventoryClient.checkStock(request.skuId(), request.quantity());
        if (!stockAvailable) {
            throw new InsufficientStockException(request.productId(), request.quantity(), 0);
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndSkuId(cartUuid, request.skuId()).orElse(null);

        CartItem savedItem;
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
            existingItem.calculateTotalPrice();
            savedItem = cartItemRepository.save(existingItem);
            log.info("Updated existing cart item: {}", savedItem.getId());
        } else {
            BigDecimal price = pricingClient.getProductPrice(request.skuId());

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(request.productId());
            newItem.setSkuId(request.skuId());
            newItem.setQuantity(request.quantity());
            newItem.setUnitPrice(price);
            newItem.calculateTotalPrice();
            
            savedItem = cartItemRepository.save(newItem);
            cart.addItem(savedItem);
            log.info("Added new cart item: {}", savedItem.getId());
        }

        inventoryClient.reserveStock(request.skuId(), request.quantity());

        return cartItemMapper.toCartItemResponse(savedItem);
    }

    @Override
    public CartItemResponse updateQuantity(String cartId, String itemId, UpdateItemQuantityRequest request) {
        log.info("Updating item {} in cart {}: new qty={}", itemId, cartId, request.quantity());

        UUID cartUuid = UUID.fromString(cartId);
        UUID itemUuid = UUID.fromString(itemId);

        cartValidator.validateAndGetActiveCart(cartUuid);

        CartItem item = cartItemRepository.findByIdAndCartId(itemUuid, cartUuid)
            .orElseThrow(() -> new ItemNotFoundException(cartUuid, itemUuid));

        if (request.quantity() == 0) {
            cartItemRepository.delete(item);
            log.info("Item removed due to zero quantity: {}", itemId);
            return null;
        }

        itemValidator.validateQuantity(request.quantity());
        item.setQuantity(request.quantity());
        item.calculateTotalPrice();
        item = cartItemRepository.save(item);

        log.info("Item quantity updated: {}", itemId);
        return cartItemMapper.toCartItemResponse(item);
    }

    @Override
    public void removeItem(String cartId, String itemId) {
        log.info("Removing item {} from cart {}", itemId, cartId);

        UUID cartUuid = UUID.fromString(cartId);
        UUID itemUuid = UUID.fromString(itemId);

        CartItem item = cartItemRepository.findByIdAndCartId(itemUuid, cartUuid)
            .orElseThrow(() -> new ItemNotFoundException(cartUuid, itemUuid));

        cartItemRepository.delete(item);
        log.info("Item removed: {}", itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(String cartId) {
        UUID cartUuid = UUID.fromString(cartId);
        cartValidator.validateAndGetActiveCart(cartUuid);
        
        List<CartItem> items = cartItemRepository.findByCartId(cartUuid);
        return cartItemMapper.toCartItemResponseList(items);
    }

    @Override
    public void clearCart(String cartId) {
        log.info("Clearing cart: {}", cartId);
        UUID cartUuid = UUID.fromString(cartId);
        cartItemRepository.deleteAllByCartId(cartUuid);
        log.info("Cart cleared: {}", cartId);
    }
}