package com.vdtry06.ecommerce.category;

import com.vdtry06.ecommerce.product.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue
    Integer id;
    String name;
    String description;
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)// có thể loại bỏ remove
    List<Product> products;
}
