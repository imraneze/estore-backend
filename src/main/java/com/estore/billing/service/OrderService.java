package com.estore.billing.service;

import com.estore.billing.dto.*;
import com.estore.billing.entity.*;
import com.estore.billing.repository.*;
import com.estore.customer.entity.User;
import com.estore.customer.repository.UserRepository;
import com.estore.exception.*;
import com.estore.inventory.service.InventoryService;
import com.estore.shopping.entity.*;
import com.estore.shopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Cart cart = cartService.findOrCreateCart(request.getUserId());

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place an order with an empty cart");
        }

        // verify stock for all items before doing anything
        for (CartItem cartItem : cart.getItems()) {
            if (!inventoryService.isAvailable(cartItem.getProduct().getId(), cartItem.getQuantity())) {
                throw new BadRequestException("Insufficient stock for product: " + cartItem.getProduct().getName());
            }
        }

        // build order
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CONFIRMED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getUnitPrice())
                        .build())
                .toList();

        BigDecimal total = orderItems.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        order.setItems(orderItems);
        orderRepository.save(order);

        // reduce stock and clear cart
        for (CartItem cartItem : cart.getItems()) {
            inventoryService.reduceStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }
        cartService.clearCart(cart);

        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(itemResponses)
                .build();
    }
}