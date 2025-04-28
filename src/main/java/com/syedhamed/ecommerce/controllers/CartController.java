package com.syedhamed.ecommerce.controllers;


import com.syedhamed.ecommerce.payload.APIResponse;
import com.syedhamed.ecommerce.payload.CartDTO;
import com.syedhamed.ecommerce.payload.CartRequest;
import com.syedhamed.ecommerce.payload.CartItemDTO;
import com.syedhamed.ecommerce.service.contract.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    //add product, and it's quantity to a cart by referencing it to a cartItem
    @PostMapping("/cart/items")
    public ResponseEntity<APIResponse<CartItemDTO>> addProductToCart(@RequestBody @Valid CartRequest cartRequest){
        CartItemDTO cartItemDto = cartService.addProductToCart(cartRequest.getProductId(), cartRequest.getQuantity(), cartRequest.getNote());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(cartItemDto, "Added Successfully" , true));
    }

    @PatchMapping("/cart/items")
    public ResponseEntity<APIResponse<CartItemDTO>> updateCartAfterRemoval(@RequestBody @Valid CartRequest cartRequest){
        CartItemDTO cartItemDTO = cartService.deleteProductFromCart(cartRequest.getProductId(), cartRequest.getQuantity());
        String message = (cartItemDTO == null)
                ? "Product removed completely from cart"
                : "Product quantity updated in cart";
        return ResponseEntity.status(HttpStatus.OK)
                .body(new APIResponse(cartItemDTO, message, true));
    }

    @GetMapping("/cart")
    public ResponseEntity<APIResponse<CartDTO>> getCurrentUserCart(){
            return new ResponseEntity<>
                    (new APIResponse<>(cartService
                            .getCurrentUserCart(),
                            "Fetched Successfully",
                            true), HttpStatus.OK);
    }

    @GetMapping("/carts/user/{userId}")
    public ResponseEntity<APIResponse<CartDTO>> getCartbyUserId(@PathVariable Long userId){
        return ResponseEntity
                .ok(new APIResponse<>(cartService.getCartByUserId(userId), "Fetched Successfully", true));
    }

    @GetMapping("/carts")
    public ResponseEntity<APIResponse<List<CartDTO>>> getAllUserCarts(){
        return ResponseEntity
                .ok(new APIResponse<>(cartService.getAllUserCarts(), "Fetched Successfully", true));
    }

}
