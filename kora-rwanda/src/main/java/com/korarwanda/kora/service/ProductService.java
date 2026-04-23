package com.korarwanda.kora.service;

import com.korarwanda.kora.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto.Response create(Long artisanId, ProductDto.Request request);
    ProductDto.Response getById(Long id);
    List<ProductDto.Response> getAll();
    List<ProductDto.Response> getAvailable();
    List<ProductDto.Response> getByArtisan(Long artisanId);
    List<ProductDto.Response> search(String keyword);
    ProductDto.Response update(Long id, ProductDto.Request request);
    List<ProductDto.Response> createBulk(Long artisanId, List<ProductDto.Request> requests);
    void delete(Long id);
}
