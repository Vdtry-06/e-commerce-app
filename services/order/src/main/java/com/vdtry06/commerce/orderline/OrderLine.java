package com.vdtry06.commerce.orderline;

import com.vdtry06.commerce.order.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "customer_line")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderLine {

    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
    Integer productId;
    double quantity;
}
