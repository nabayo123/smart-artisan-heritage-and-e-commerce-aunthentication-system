package com.korarwandasystem.korarwanda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_ITEMS")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity")
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "1", inclusive = true, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "unit_price", precision = 12, scale = 2)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    @Column(name = "heritage_hash", length = 255)
    private String heritageHash;

    public OrderItem() {}

    public OrderItem(Order order, Product product, Integer quantity, BigDecimal unitPrice, String heritageHash) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.heritageHash = heritageHash;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity.longValue()));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getHeritageHash() { return heritageHash; }
    public void setHeritageHash(String heritageHash) { this.heritageHash = heritageHash; }

    // Convenience method
    public BigDecimal getPrice() { return unitPrice; }

    // JSON mapping
    @JsonProperty("order_id")
    public void setOrderId(Long orderId) {
        if (orderId != null) {
            if (this.order == null) this.order = new Order();
            this.order.setId(orderId);  // <-- FIXED
        }
    }

    @JsonProperty("product_id")
    public void setProductId(Long productId) {
        if (productId != null) {
            if (this.product == null) this.product = new Product();
            this.product.setId(productId);  // <-- FIXED
        }
    }
}