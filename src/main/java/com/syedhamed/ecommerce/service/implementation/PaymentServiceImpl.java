package com.syedhamed.ecommerce.service.implementation;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.syedhamed.ecommerce.exceptions.APIException;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.Order;
import com.syedhamed.ecommerce.model.Payment;
import com.syedhamed.ecommerce.payload.external.PaymentResponse;
import com.syedhamed.ecommerce.repository.PaymentRepository;
import com.syedhamed.ecommerce.service.contract.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    @Value("${pg.key.id}")
    private String razorPayKey;
    @Value("${pg.key.secret}")
    private String razorPaySecret;

    @Override
    public Payment createPaymentOrder(Order order) {
        RazorpayClient razorpayClient;
        try {
            razorpayClient = new RazorpayClient(razorPayKey, razorPaySecret, true);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        razorpayClient.addHeaders(headers);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", order.getTotalPrice().multiply(BigDecimal.valueOf(100)));
        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "receipt#1");
//        JSONObject notes = new JSONObject();
//        for (OrderItem orderItem : order.getOrderItems()) {
//            notes.put("notes_key_1", "Tea, Earl Grey, Hot");
//        }
//        orderRequest.put("notes", notes);
        com.razorpay.Order razorOrder;
        try {
            razorOrder = razorpayClient.orders.create(orderRequest);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentOrderId(razorOrder.get("id"));
        payment.setCurrency(razorOrder.get("currency"));
        log.info("created at type : [{}]", razorOrder.get("created_at").getClass());
        Date createdAt = razorOrder.get("created_at");
        LocalDateTime createdAt1 = createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        payment.setCreatedAt(createdAt1);
        Number amountNumber = razorOrder.get("amount");
        // need to divide by 100 since razorpay response includes amount in paise not Rupees
        BigDecimal amount = BigDecimal.valueOf(amountNumber.longValue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        payment.setAmount(amount);
//        payment.setReceipt(razorOrder.get("receipt"));
        return paymentRepository.save(payment);
    }
//   Orders entity example
//    {
//        "amount": 100000,
//            "amount_due": 100000,
//            "amount_paid": 0,
//            "attempts": 0,
//            "created_at": 1746942643,
//            "currency": "INR",
//            "entity": "order",
//            "id": "order_QTVq7ToOgFZzdP",
//            "notes": {
//        "key1": "value3",
//                "key2": "value2"
//    },
//        "offer_id": null,
//            "receipt": "receipt#2",
//            "status": "created"
//    }


    @Override
    public void verifyPayment(PaymentResponse paymentResponse) {
        Payment payment = paymentRepository
                .findByPaymentOrderId(paymentResponse.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("payment", "orderId", paymentResponse.getRazorpayOrderId()));
        if (!verifySignature(payment, paymentResponse)) {
            throw new APIException("Invalid payment signature");
        }
        payment.setPaymentId(paymentResponse.getRazorpayPaymentId());
        payment.setSignature(paymentResponse.getRazorpaySignature());
        paymentRepository.save(payment);
    }

    private boolean verifySignature(Payment payment, PaymentResponse paymentResponse) {
        String payload = payment.getPaymentOrderId() + "|" + paymentResponse.getRazorpayPaymentId();
        String expected = hmacSHA256(payload, razorPaySecret);
       return expected.equals(paymentResponse.getRazorpaySignature());
    }


    private String hmacSHA256(String data, String key) {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        try {
            mac.init(secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = mac.doFinal(data.getBytes());
        return new String(Hex.encode(hash));
    }

}
