package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Product;
import com.korarwandasystem.korarwanda.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Basic CRUD operations inherited from JpaRepository
    
    // Custom queries for product management
    List<Product> findByArtisan_ArtisanId(Long artisanId);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByStatus(ProductStatus status);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByArtisan_VerificationStatus(com.korarwandasystem.korarwanda.model.VerificationStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.artisan.cooperative.province = :province")
    List<Product> findByArtisanProvince(@Param("province") String province);
    
    @Query("SELECT p FROM Product p WHERE p.artisan.cooperative.district = :district")
    List<Product> findByArtisanDistrict(@Param("district") String district);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    long countByStatus(@Param("status") ProductStatus status);
    
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findLatestProducts();
}
