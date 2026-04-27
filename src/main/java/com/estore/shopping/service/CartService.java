package com.estore.shopping.service;

import com.estore.catalog.entity.Product;
import com.estore.catalog.repository.ProductRepository;
import com.estore.customer.entity.User;
import com.estore.customer.repository.UserRepository;
import com.estore.exception.*;
import com.estore.inventory.service.InventoryService;
import com.estore.shopping.dto.*;
import com.estore.shopping.entity.*;
import com.estore.shopping.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public CartResponse getCartByUserId(Long userId) {
        Cart cart = findOrCreateCart(userId);
        return toResponse(cart);
    }

    public CartResponse addToCart(AddToCartRequest request) {
        Cart cart = findOrCreateCart(request.getUserId());
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (!inventoryService.isAvailable(product.getId(), request.getQuantity())) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .unitPrice(product.getPrice())
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    public CartResponse updateItem(Long itemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!inventoryService.isAvailable(item.getProduct().getId(), request.getQuantity())) {
            throw new BadRequestException("Insufficient stock for product: " + item.getProduct().getName());
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(item.getCart());
    }

    public void removeItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));
        cartItemRepository.delete(item);
    }

    public void clearCart(Cart cart) {
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart findOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            return cartRepository.save(Cart.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build());
        });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemResponses)
                .total(total)
                .build();
    }
}