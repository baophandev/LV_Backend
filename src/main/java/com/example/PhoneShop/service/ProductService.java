package com.example.PhoneShop.service;


import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.request.AttributRequest;
import com.example.PhoneShop.dto.request.ProductVariant.CreateDiscountRequest;
import com.example.PhoneShop.dto.request.ProductVariant.CreateVariantRequest;
import com.example.PhoneShop.dto.request.ProductVariant.UpdateVariantRequest;
import com.example.PhoneShop.dto.request.UpdateProductRequest;
import com.example.PhoneShop.dto.response.AttributeResponse;
import com.example.PhoneShop.dto.response.DiscountResponse;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.DiscountMapper;
import com.example.PhoneShop.mapper.ProductMapper;
import com.example.PhoneShop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;
    ProductVariantRepository productVariantRepository;
    DiscountRepository discountRepository;
    DiscountMapper discountMapper;
    PriceHistoryRepository priceHistoryRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ProductResponse create(CreateProductRequest request, List<MultipartFile> files) throws IOException {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category not found", "category-e-01"));

        product.setCategory(category);
        product.setStatus(ProductStatus.DRAFT);
        product.setRating(0.0);
        product.setSold(0);
        product.setCreatedAt(LocalDateTime.now());
        List<Review> reviews = new ArrayList<>();
        product.setReviews(reviews);
        List<Image> images = new ArrayList<>();

        for (MultipartFile file : files) {
            Image image = Image.builder()
                    .imageType(file.getContentType())
                    .data(file.getBytes())
                    .product(product)
                    .build();
            images.add(image);
        }
        product.setImages(images);

        return productMapper.toProductResponse(productRepository.save(product));

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ProductResponse updateProduct(
            String id,
            UpdateProductRequest request,
            List<MultipartFile> files) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product not found", "product-e-01"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setStatus(request.getStatus());
        product.setRelated_id(request.getRelated_id());

        if(request.getRemoveImageIds() != null && !request.getRemoveImageIds().isEmpty()){
            product.getImages().removeIf(image -> request.getRemoveImageIds().contains(image.getId()));
        }

        if (product.getImages().isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Product must have at least one image", "product-e-02");
        }

        if(files != null && !files.isEmpty()){
            for(MultipartFile file : files){
                if (!file.isEmpty()) {
                    Image image = Image.builder()
                            .imageType(file.getContentType())
                            .data(file.getBytes())
                            .product(product)
                            .build();
                    product.getImages().add(image);
                }
            }
        }

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public AttributeResponse updateProductAttribute(String prdId, AttributRequest updateProductAtrRequest){
        Product product = productRepository.findById(prdId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product not found", "product-e-01"));

        Attribute attribute = product.getAttribute();
        if(attribute == null){
            attribute = new Attribute();
            attribute.setProduct(product);
        }
        attribute.setOs(updateProductAtrRequest.getOs());
        attribute.setCpu(updateProductAtrRequest.getCpu());
        attribute.setRam(updateProductAtrRequest.getRam());
        attribute.setRom(updateProductAtrRequest.getRom());
        attribute.setPin(updateProductAtrRequest.getPin());
        attribute.setCamera(updateProductAtrRequest.getCamera());
        attribute.setSim(updateProductAtrRequest.getSim());
        attribute.setOthers(updateProductAtrRequest.getOthers());

        product.setAttribute(attribute);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toAttributeResponse(updatedProduct.getAttribute());
    }

    public CustomPageResponse<ProductResponse> getAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = products.getContent()
                .stream().map(productMapper::toProductResponse).toList();

        return CustomPageResponse.<ProductResponse>builder()
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .content(productResponses)
                .build();
    }

    public CustomPageResponse<ProductResponse> getByStatus(ProductStatus status, Pageable pageable){
        Page<Product> products = productRepository.findByStatus(status, pageable);

        List<ProductResponse> productResponses = products.getContent()
                .stream().map(productMapper::toProductResponse).toList();

        return CustomPageResponse.<ProductResponse>builder()
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .content(productResponses)
                .build();
    }

    public CustomPageResponse<ProductResponse> getByStatusAndCategoryId(ProductStatus status, String categoryId, Pageable pageable){
        Page<Product> products;

        if(status == null){
            products = productRepository.findByCategoryId(categoryId, pageable);
        }else{
            products = productRepository.findByStatusAndCategoryId(status, categoryId, pageable);
        }

        List<ProductResponse> productResponses = products.getContent()
                .stream().map(productMapper::toProductResponse).toList();

        return CustomPageResponse.<ProductResponse>builder()
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .content(productResponses)
                .build();
    }


    public ProductResponse getById(String id) {
        return productMapper.toProductResponse(productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist!", "product-e-02")));
    }

    public AttributeResponse getAtrByPrdId (String productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product not found", "product-e-01"));

        return productMapper.toAttributeResponse(product.getAttribute());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void delete(String id) {
        productRepository.deleteById(id);
    }


    /*
    * Product variant
    */
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Long createProductVariant(CreateVariantRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist", "product-e-01"));

        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .price(request.getPrice())
                .color(request.getColor())
                .discount(0)
                .sold(0)
                .stock(request.getStock())
                .build();

        productVariantRepository.save(productVariant);

        return  productVariant.getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String updateProductVariant(Long id, UpdateVariantRequest request) {
        // Lấy thông tin ProductVariant hiện tại
        ProductVariant productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Variant does not exist", "variant-e-01"));

        // Kiểm tra xem giá có thay đổi không
        boolean priceChanged = !productVariant.getPrice().equals(request.getPrice());

        // Cập nhật các trường không liên quan đến giá
        productVariant.setSold(request.getSold());
        productVariant.setColor(request.getColor());
        productVariant.setStock(request.getStock());

        // Nếu giá mới khác giá cũ thì tiến hành cập nhật giá và lưu lịch sử
        if (priceChanged) {
            LocalDateTime updateTime = LocalDateTime.now();

            // Lấy thông tin bản ghi giá hiện tại (bản ghi có endDate == null)
            Optional<PriceHistory> currPriceHistoryOpt = priceHistoryRepository
                    .findCurrentPriceHistoryByProductVariantId(id);

            // Cập nhật giá mới cho variant
            productVariant.setPrice(request.getPrice());
            productVariantRepository.save(productVariant);

            // Nếu có bản ghi giá hiện tại và endDate chưa được set, cập nhật endDate bằng thời điểm update
            if (currPriceHistoryOpt.isPresent()) {
                PriceHistory currPriceHistory = currPriceHistoryOpt.get();
                if (currPriceHistory.getEndDate() == null) {
                    currPriceHistory.setEndDate(updateTime);
                    priceHistoryRepository.save(currPriceHistory);
                }
            }

            // Tạo bản ghi mới cho giá mới với startDate = thời điểm cập nhật và endDate = null
            PriceHistory newPriceHistory = PriceHistory.builder()
                    .productVariant(productVariant)
                    .price(productVariant.getPrice())  // Giả sử field đã được đổi tên thành 'price'
                    .startDate(updateTime)
                    .endDate(null)
                    .build();
            priceHistoryRepository.save(newPriceHistory);
        } else {
            // Nếu giá không thay đổi, chỉ cập nhật các thông tin khác
            productVariantRepository.save(productVariant);
        }

        return "Update variant successfully";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void deleteVariant(String productId, Long variantId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist", "product-e-01"));
        boolean removed = product.getVariants().removeIf(variant -> variant.getId().equals(variantId));

        if(!removed){
            throw new AppException(HttpStatus.NOT_FOUND, "Variant does not exist", "variant-e-02");
        }

        if(product.getVariants().isEmpty()){
            throw  new AppException(HttpStatus.BAD_REQUEST, "Product must have at least one variant", "product-e-03");
        }
        productRepository.save(product);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public DiscountResponse createDiscount(CreateDiscountRequest request){
        ProductVariant productVariant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Variant does not exist", "variant-e-01"));

        Discount discount = Discount.builder()
                .discountValue(request.getDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .productVariant(productVariant)
                .build();

        return discountMapper.toDiscountResponse(discountRepository.save(discount));
    }

    public DiscountResponse getActiveDiscount(Long variantId) {
        LocalDateTime now = LocalDateTime.now();

        Optional<Discount> discountOptional = discountRepository
                .findFirstByProductVariant_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                        variantId, now, now);

        if (discountOptional.isPresent()) {
            Discount discount = discountOptional.get();
            return DiscountResponse.builder()
                    .discountId(discount.getId())
                    .discountValue(discount.getDiscountValue())
                    .startDate(discount.getStartDate())
                    .endDate(discount.getEndDate())
                    // Lấy variantId từ đối tượng ProductVariant (giả sử id là kiểu Long)
                    .variantId(discount.getProductVariant().getId())
                    .build();
        }

        // Nếu không có discount nào còn hiệu lực, trả về discountValue = 0 (các trường khác có thể là null)
        return DiscountResponse.builder()
                .discountId(null)
                .discountValue(0)
                .startDate(null)
                .endDate(null)
                .variantId(variantId)
                .build();
    }

    public List<DiscountResponse> getAllDiscountsByVariant(Long variantId) {
        List<Discount> discounts = discountRepository.findByProductVariant_Id(variantId);
        return discounts.stream()
                .map(discountMapper::toDiscountResponse)
                .collect(Collectors.toList());
    }
}
