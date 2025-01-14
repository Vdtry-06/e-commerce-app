package com.vdtry06.commerce.payment;

import com.vdtry06.commerce.customer.CustomerResponse;
import com.vdtry06.commerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}