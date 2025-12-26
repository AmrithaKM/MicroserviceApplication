package com.microservice.productService.controller;

import com.microservice.productService.dto.ProductRequest;
import com.microservice.productService.dto.ProductResponse;
import com.microservice.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProducts(@RequestBody List<ProductRequest> productRequests) {
        productService.createProducts(productRequests);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductBySkuCode(@PathVariable String skuCode) {
        return productService.getProductBySkuCode(skuCode);
    }
}
