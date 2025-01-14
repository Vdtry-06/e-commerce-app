package com.vdtry06.commerce.kafka;

import com.vdtry06.commerce.customer.CustomerResponse;
import com.vdtry06.commerce.order.PaymentMethod;
import com.vdtry06.commerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}