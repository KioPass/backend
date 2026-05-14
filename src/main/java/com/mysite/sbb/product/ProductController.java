package com.mysite.sbb.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.sbb.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store/{storeId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 매장 상품 목록 조회 / 바코드로 단건 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @PathVariable Long storeId,
            @RequestParam(required = false) String barcode) {

        if (barcode != null) {
            ProductResponse product = productService.getProductByBarcode(storeId, barcode);
            return ResponseEntity.ok(ApiResponse.success(List.of(product)));
        }
        return ResponseEntity.ok(ApiResponse.success(productService.getProducts(storeId)));
    }

    // 상품 추가
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @PathVariable Long storeId,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.createProduct(storeId, request)));
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(productId, request)));
    }

    // 상품 이미지 업로드
    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestPart("image") MultipartFile image) {
        String url = productService.uploadImage(storeId, productId, image);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
