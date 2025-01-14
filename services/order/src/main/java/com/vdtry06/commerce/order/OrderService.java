package com.vdtry06.commerce.order;


import com.vdtry06.commerce.customer.CustomerClient;
import com.vdtry06.commerce.exception.BusinessException;
import com.vdtry06.commerce.kafka.OrderConfirmation;
import com.vdtry06.commerce.kafka.OrderProducer;
import com.vdtry06.commerce.orderline.OrderLineRequest;
import com.vdtry06.commerce.orderline.OrderLineService;
import com.vdtry06.commerce.payment.PaymentClient;
import com.vdtry06.commerce.payment.PaymentRequest;
import com.vdtry06.commerce.product.ProductClient;
import com.vdtry06.commerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository repository;
    OrderMapper mapper;
    CustomerClient customerClient;
    PaymentClient paymentClient;
    ProductClient productClient;
    OrderLineService orderLineService;
    OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        // 1. check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        // 2. purchase the products --> product-ms
        var purchasedProducts = productClient.purchaseProducts(request.products());

        // 3. persist order
        var order = this.repository.save(mapper.toOrder(request));

        // 4. persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // 5. todo start payment process
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );


        // 6. send the order confirmation --> notification-ms (kafka)
        paymentClient.requestOrderPayment(paymentRequest);

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
