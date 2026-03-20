package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.Artisan;
import com.korarwandasystem.korarwanda.model.Product;
import com.korarwandasystem.korarwanda.model.ProductStatus;
import com.korarwandasystem.korarwanda.repository.ArtisanRepository;
import com.korarwandasystem.korarwanda.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ArtisanRepository artisanRepository;

    public ProductService(ProductRepository productRepository, ArtisanRepository artisanRepository) {
        this.productRepository = productRepository;
        this.artisanRepository = artisanRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.AVAILABLE);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        if (productDetails == null) {
            throw new IllegalArgumentException("Product details cannot be null");
        }
        
        return productRepository.findById(id).map(product -> {
            if (productDetails.getName() != null) {
                product.setName(productDetails.getName());
            }
            if (productDetails.getDescription() != null) {
                product.setDescription(productDetails.getDescription());
            }
            if (productDetails.getPrice() != null) {
                product.setPrice(productDetails.getPrice());
            }
            if (productDetails.getStockQuantity() != null) {
                product.setStockQuantity(productDetails.getStockQuantity());
            }
            if (productDetails.getCategory() != null) {
                product.setCategory(productDetails.getCategory());
            }
            if (productDetails.getMaterials() != null) {
                product.setMaterials(productDetails.getMaterials());
            }
            if (productDetails.getDimensions() != null) {
                product.setDimensions(productDetails.getDimensions());
            }
            if (productDetails.getWeight() != null) {
                product.setWeight(productDetails.getWeight());
            }
            if (productDetails.getImageUrl() != null) {
                product.setImageUrl(productDetails.getImageUrl());
            }
            if (productDetails.getStatus() != null) {
                product.setStatus(productDetails.getStatus());
            }

            if (productDetails.getArtisan() != null && productDetails.getArtisan().getId() != null) {
                artisanRepository.findById(productDetails.getArtisan().getId())
                        .ifPresent(product::setArtisan);
            }

            return productRepository.save(product);
        }).orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Product> getProductsByArtisanId(Long artisanId) {
        return productRepository.findByArtisanId(artisanId);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByStatus(ProductStatus.AVAILABLE);
    }

    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsByMaterial(String material) {
        return productRepository.findByMaterialsContainingIgnoreCase(material);
    }

    public long getTotalProductsCount() {
        return productRepository.count();
    }

    public long getAvailableProductsCount() {
        return productRepository.countByStatus(ProductStatus.AVAILABLE);
    }

    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    public Product updateProductStock(Long productId, int quantity) {
        return productRepository.findById(productId).map(product -> {
            product.setStockQuantity(quantity);
            // Logic to auto-flip status based on quantity
            if (quantity <= 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            } else {
                product.setStatus(ProductStatus.AVAILABLE);
            }
            return productRepository.save(product);
        }).orElse(null);
    }

    // Added methods for secure CRUD operations
    public List<Product> getProductsByArtisanEmail(String email) {
        Optional<Artisan> artisan = artisanRepository.findByEmail(email);
        return artisan.map(art -> productRepository.findByArtisanId(art.getId()))
                     .orElse(List.of());
    }

    public Optional<Artisan> getArtisanByEmail(String email) {
        return artisanRepository.findByEmail(email);
    }

    public boolean isOwner(Long productId, String email) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Product foundProduct = product.get();
            Artisan artisan = foundProduct.getArtisan();
            if (artisan != null) {
                return email.equals(artisan.getEmail());
            }
        }
        return false;
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}