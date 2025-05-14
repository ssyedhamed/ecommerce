package com.syedhamed.ecommerce.service.contract;

import com.razorpay.RazorpayException;
import com.syedhamed.ecommerce.model.Order;
import com.syedhamed.ecommerce.model.Payment;
import com.syedhamed.ecommerce.payload.external.PaymentResponse;

public interface PaymentService {
    Payment createPaymentOrder(Order order);

    boolean verifyPayment(PaymentResponse paymentResponse);
}
