package com.korarwandasystem.korarwanda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("product_id")
    private Long id;  // <-- standardize as id

    @JsonProperty("name")
    private String name;

    @Column(columnDefinition = "TEXT", name = "description")
    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    @JsonProperty("category")
    private String category;

    @JsonProperty("materials")
    private String materials;

    @JsonProperty("dimensions")
    private String dimensions;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id")
    @JsonProperty("artisan")
    private Artisan artisan;

    public Product() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMaterials() { return materials; }
    public void setMaterials(String materials) { this.materials = materials; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public Artisan getArtisan() { return artisan; }
    public void setArtisan(Artisan artisan) { this.artisan = artisan; }

    // JSON mapping for artisan_id
    @JsonProperty("artisan_id")
    public void setArtisanId(Long artisanId) {
        if (artisanId != null) {
            if (this.artisan == null) {
                this.artisan = new Artisan();
            }
            this.artisan.setId(artisanId);  // <-- use standard id
        }
    }
}