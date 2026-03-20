package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Product;
import com.korarwandasystem.korarwanda.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByArtisanId(Long artisanId);

    List<Product> findByCategory(String category);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByMaterialsContainingIgnoreCase(String material);

    long countByStatus(ProductStatus status);

    boolean existsByName(String name);
}