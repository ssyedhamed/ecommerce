package com.syedhamed.ecommerce.controllers;

import com.syedhamed.ecommerce.payload.APIResponse;
import com.syedhamed.ecommerce.payload.external.PaymentResponse;
import com.syedhamed.ecommerce.service.contract.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentResponse paymentResponse) {
        paymentService.verifyPayment(paymentResponse);
        return new ResponseEntity<>(new APIResponse<>("Payment verified", true), HttpStatus.OK);
    }
}
