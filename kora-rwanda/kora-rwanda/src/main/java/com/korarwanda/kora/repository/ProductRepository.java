package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Product;
import com.korarwanda.kora.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByStatus(ProductStatus status);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByArtisan_ArtisanId(Long artisanId);
    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.status = 'AVAILABLE' AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchByKeyword(String keyword);
}
