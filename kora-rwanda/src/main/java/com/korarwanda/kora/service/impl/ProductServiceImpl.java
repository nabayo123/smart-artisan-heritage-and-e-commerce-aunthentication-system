package com.korarwanda.kora.service.impl;

import com.korarwanda.kora.dto.ProductDto;
import com.korarwanda.kora.entity.Artisan;
import com.korarwanda.kora.entity.Certificate;
import com.korarwanda.kora.entity.Product;
import com.korarwanda.kora.enums.CertificateStatus;
import com.korarwanda.kora.enums.ProductStatus;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.ArtisanRepository;
import com.korarwanda.kora.repository.CertificateRepository;
import com.korarwanda.kora.repository.ProductRepository;
import com.korarwanda.kora.service.ProductService;
import com.korarwanda.kora.util.HeritageTagUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private ArtisanRepository artisanRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private HeritageTagUtil heritageTagUtil;

    @Override
    @Transactional
    public ProductDto.Response create(Long artisanId, ProductDto.Request request) {
        Artisan artisan = artisanRepository.findById(artisanId)
                .orElseThrow(() -> new ResourceNotFoundException("Artisan", artisanId));

        Product product = Product.builder()
                .artisan(artisan)
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(ProductStatus.AVAILABLE)
                .build();

        product = productRepository.save(product);

        // Auto-generate Heritage Tag (Certificate of Authenticity)
        String district = artisan.getDistrictVillage() != null ? artisan.getDistrictVillage() : "Rwanda";
        String heritageHash = heritageTagUtil.generateHeritageHash(artisanId, request.getName(), district);
        String qrCodeData = heritageTagUtil.generateQrCodeData(heritageHash);
        String qrCodeBase64 = heritageTagUtil.generateQrCodeBase64(qrCodeData);

        Certificate certificate = Certificate.builder()
                .product(product)
                .heritageHash(heritageHash)
                .qrCodeData(qrCodeData)
                .qrCodeImageUrl(qrCodeBase64)
                .issueDate(LocalDate.now())
                .verificationStatus(CertificateStatus.VALID)
                .build();

        certificateRepository.save(certificate);

        return toResponse(product, certificate);
    }

    @Override
    public ProductDto.Response getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        Certificate cert = certificateRepository.findByProduct_ProductId(id).orElse(null);
        return toResponse(product, cert);
    }

    @Override
    public List<ProductDto.Response> getAll() {
        return productRepository.findAll().stream()
                .map(p -> toResponse(p, certificateRepository.findByProduct_ProductId(p.getProductId()).orElse(null)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto.Response> getAvailable() {
        return productRepository.findByStatus(ProductStatus.AVAILABLE).stream()
                .map(p -> toResponse(p, certificateRepository.findByProduct_ProductId(p.getProductId()).orElse(null)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto.Response> getByArtisan(Long artisanId) {
        return productRepository.findByArtisan_ArtisanId(artisanId).stream()
                .map(p -> toResponse(p, certificateRepository.findByProduct_ProductId(p.getProductId()).orElse(null)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto.Response> search(String keyword) {
        return productRepository.searchByKeyword(keyword).stream()
                .map(p -> toResponse(p, certificateRepository.findByProduct_ProductId(p.getProductId()).orElse(null)))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto.Response update(Long id, ProductDto.Request request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        Certificate cert = certificateRepository.findByProduct_ProductId(id).orElse(null);
        return toResponse(productRepository.save(product), cert);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) throw new ResourceNotFoundException("Product", id);
        productRepository.deleteById(id);
    }

    private ProductDto.Response toResponse(Product p, Certificate cert) {
        ProductDto.Response r = new ProductDto.Response();
        r.setProductId(p.getProductId());
        r.setName(p.getName());
        r.setCategory(p.getCategory());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setStatus(p.getStatus());
        r.setImageUrl(p.getImageUrl());
        r.setCreatedAt(p.getCreatedAt());
        if (p.getArtisan() != null) {
            r.setArtisanId(p.getArtisan().getArtisanId());
            r.setArtisanName(p.getArtisan().getFullName());
            r.setArtisanDistrict(p.getArtisan().getDistrictVillage());
        }
        if (cert != null) {
            r.setHeritageHash(cert.getHeritageHash());
        }
        return r;
    }
}
