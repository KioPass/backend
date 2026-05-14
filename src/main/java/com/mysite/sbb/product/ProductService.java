package com.mysite.sbb.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.sbb.file.FileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileService fileService;

    public List<ProductResponse> getProducts(Long storeId) {
        return productRepository.findByStoreId(storeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductByBarcode(Long storeId, String barcode) {
        Product product = productRepository.findByStoreIdAndBarcode(storeId, barcode)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        return toResponse(product);
    }

    public ProductResponse createProduct(Long storeId, ProductRequest request) {
        Product product = new Product();
        product.setStoreId(storeId);
        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        productRepository.save(product);
        return toResponse(product);
    }

    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        productRepository.save(product);
        return toResponse(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public String uploadImage(Long storeId, Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        String url = fileService.upload(image, productId, "product_" + storeId);
        product.setImageUrl(url);
        productRepository.save(product);
        return url;
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .barcode(product.getBarcode())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .lowStock(product.getStock() <= 10)
                .imageUrl(product.getImageUrl())
                .build();
    }
}
