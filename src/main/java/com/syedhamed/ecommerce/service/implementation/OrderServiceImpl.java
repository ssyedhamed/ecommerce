package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.enums.OrderStatus;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.*;
import com.syedhamed.ecommerce.payload.OrderDTO;
import com.syedhamed.ecommerce.payload.OrderItemDTO;
import com.syedhamed.ecommerce.payload.ProductSnapshot;
import com.syedhamed.ecommerce.repository.CartRepository;
import com.syedhamed.ecommerce.repository.OrderRepository;
import com.syedhamed.ecommerce.repository.ProductRepository;
import com.syedhamed.ecommerce.repository.ProductSnapshotRepository;
import com.syedhamed.ecommerce.service.contract.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ProductSnapshotRepository productSnapshotRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(User user) {

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Cart", "userID", user.getId()));
        List<CartItem> cartItems = cart.getCartItems();
        List<OrderItem> orderItems = new ArrayList<>();
        log.info("Iterating cart items...");
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            ProductSnapshot snapshot = new ProductSnapshot();
            snapshot.setProductName(product.getProductName());
            snapshot.setProductImage(product.getProductImage());
            snapshot.setPrice(product.getSpecialPrice());
            ProductSnapshot productSnapshot = productSnapshotRepository.save(snapshot);

            log.info("Product snapshot : [{}]", snapshot);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductSnapshot(productSnapshot);
            orderItem.setPriceAtPurchase(cartItem.getProduct().getSpecialPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            log.info("product snapshot [{}] for cart item [{}]", productSnapshot, cartItem);
            orderItems.add(orderItem);
            //after setting the quantity , we need to subtract the quantity from the product entity
            Integer updatedStock = product.getQuantity() - cartItem.getQuantity();
            product.setQuantity(updatedStock);
            productRepository.save(product);
        }
        Order order = new Order();
        order.setOrderItems(orderItems);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        BigDecimal totalPrice = orderItems.stream().map(orderItem ->
                        orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved to the DB. Deleting cart items...");
        cart.getCartItems().clear();
        cart.setTotalSavedPrice(null);
        cart.setTotalSpecialPrice(null);
        cartRepository.save(cart);


        //Generating response
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate(savedOrder.getOrderDate());
        orderDTO.setOrderStatus(savedOrder.getOrderStatus().name());
        orderDTO.setTotalPrice(savedOrder.getTotalPrice());

        List<OrderItemDTO> itemDTOs = savedOrder.getOrderItems().stream().map(item -> {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setProductName(item.getProductSnapshot().getProductName());
            dto.setProductImage(item.getProductSnapshot().getProductImage());
            dto.setPriceAtPurchase(item.getPriceAtPurchase());
            dto.setQuantity(item.getQuantity());
            return dto;
        }).toList();

        orderDTO.setOrderItems(itemDTOs);
        return orderDTO;

    }
}
