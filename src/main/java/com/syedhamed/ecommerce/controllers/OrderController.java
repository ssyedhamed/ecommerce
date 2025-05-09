package com.syedhamed.ecommerce.controllers;

import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.payload.APIResponse;
import com.syedhamed.ecommerce.payload.OrderDTO;
import com.syedhamed.ecommerce.security.CustomUserDetails;
import com.syedhamed.ecommerce.service.contract.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/checkout")
    public ResponseEntity<APIResponse<OrderDTO>> checkout(@AuthenticationPrincipal CustomUserDetails userDetails) {

        OrderDTO orderDTO = orderService.createOrder(userDetails.getUser());
        return ResponseEntity.ok(new APIResponse<>(orderDTO, "Order placed successfully", true));

    }
}
