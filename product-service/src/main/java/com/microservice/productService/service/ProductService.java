package com.microservice.productService.service;

import com.microservice.productService.dto.ProductRequest;
import com.microservice.productService.dto.ProductResponse;
import com.microservice.productService.exception.DuplicateSkuException;
import com.microservice.productService.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.microservice.productService.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

//    public void createProduct(ProductRequest productRequest) {
//        Product product = Product.builder()
//                .skuCode(productRequest.getSkuCode())
//                .name(productRequest.getName())
//                .description(productRequest.getDescription())
//                .price(productRequest.getPrice())
//                .build();
//
//        productRepository.save(product);
//        log.info("Product {} is saved", product.getId());
//    }

    public void createProducts(List<ProductRequest> productRequests) {
        // Check for duplicate SKU codes in the request
        for (ProductRequest request : productRequests) {
            if (productRepository.findBySkuCode(request.getSkuCode()).isPresent()) {
                throw new DuplicateSkuException("Product with SKU code '" + request.getSkuCode() + "' already exists");
            }
        }

        List<Product> products = productRequests.stream()
                .map(productRequest -> Product.builder()
                        .skuCode(productRequest.getSkuCode())
                        .name(productRequest.getName())
                        .description(productRequest.getDescription())
                        .price(productRequest.getPrice())
                        .build())
                .toList();

        productRepository.saveAll(products);
        log.info("{} products are saved", products.size());
    }
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }



    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .skuCode(product.getSkuCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public ProductResponse getProductBySkuCode(String skuCode) {
        return productRepository.findBySkuCode(skuCode)
                .map(this::mapToProductResponse)
                .orElse(null);
    }

}