package com.syedhamed.ecommerce.service.implementation;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.syedhamed.ecommerce.exceptions.APIException;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.CartItem;
import com.syedhamed.ecommerce.model.Order;
import com.syedhamed.ecommerce.model.OrderItem;
import com.syedhamed.ecommerce.model.Payment;
import com.syedhamed.ecommerce.payload.external.PaymentResponse;
import com.syedhamed.ecommerce.payload.external.paymentOrderResponse;
import com.syedhamed.ecommerce.repository.PaymentRepository;
import com.syedhamed.ecommerce.service.contract.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;

    //    @Value("${pg.create-order.URL}")
    private String RAZORPAY_API_URL = "https://api.razorpay.com/v1/orders";
    @Value("${pg.key.id}")
    private String razorPayKey;
    @Value("${pg.key.secret}")
    private String razorPaySecret;

    @Override
    public Payment createPaymentOrder(Order order) {
        RazorpayClient razorpayClient = null;
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

        com.razorpay.Order razorOrder = null;
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
        Number amountNumber  = razorOrder.get("amount");
        // need to divide by 100 since razorpay response includes amount in paise not Rupees
        BigDecimal amount = BigDecimal.valueOf(amountNumber.longValue()).divide(BigDecimal.valueOf(100));
        payment.setAmount(amount);
//        payment.setReceipt(razorOrder.get("receipt"));
        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment;
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
    public boolean verifyPayment(PaymentResponse paymentResponse) {
        log.info("verifying payment...");
        Payment payment = paymentRepository
                .findByPaymentOrderId(paymentResponse.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("payment", "orderId", paymentResponse.getRazorpayOrderId()));
        log.info("payment details fetched from DB : [{}]", payment.getPaymentOrderId());
        try {
            String payload = payment.getPaymentOrderId() + "|" + paymentResponse.getRazorpayPaymentId();
            log.info("payload generated: {}", payload);
            String expected = hmacSHA256(payload, razorPaySecret);
            log.info("expected signature: {}", expected);
            log.info("original signature: {}",paymentResponse.getRazorpaySignature());
            payment.setPaymentId(paymentResponse.getRazorpayPaymentId());
            payment.setSignature(paymentResponse.getRazorpaySignature());
            paymentRepository.save(payment);
            return expected.equals(paymentResponse.getRazorpaySignature());
        } catch (Exception e) {
            return false;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes());
        return new String(Hex.encode(hash));
    }

}
