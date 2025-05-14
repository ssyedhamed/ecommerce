package com.syedhamed.ecommerce.service.contract;

import com.syedhamed.ecommerce.model.Order;
import com.syedhamed.ecommerce.model.Payment;
import com.syedhamed.ecommerce.payload.external.PaymentResponse;

public interface PaymentService {
    Payment createPaymentOrder(Order order);
    void verifyPayment(PaymentResponse paymentResponse);
}
