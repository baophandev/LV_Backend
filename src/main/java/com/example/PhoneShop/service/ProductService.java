package com.example.PhoneShop.service;


import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.request.AttributRequest;
import com.example.PhoneShop.dto.request.ProductVariant.CreateDiscountRequest;
import com.example.PhoneShop.dto.request.ProductVariant.CreateVariantRequest;
import com.example.PhoneShop.dto.request.ProductVariant.UpdateVariantRequest;
import com.example.PhoneShop.dto.request.UpdateProductRequest;
import com.example.PhoneShop.dto.response.*;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.DiscountMapper;
import com.example.PhoneShop.mapper.PriceMapper;
import com.example.PhoneShop.mapper.ProductMapper;
import com.example.PhoneShop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    PriceMapper priceMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ProductResponse create(CreateProductRequest request, MultipartFile avatar) throws IOException {
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
        new ProductAvatar();
        ProductAvatar avatarFile = ProductAvatar.builder().
                imageType(avatar.getContentType())
                .data(avatar.getBytes())
                .product(product)
                .build();

        product.setProductAvatar(avatarFile);

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ProductResponse updateProduct(
            String id,
            UpdateProductRequest request,
            MultipartFile avatar) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product not found", "product-e-01"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setStatus(request.getStatus());
        ProductAvatar productAvatar = ProductAvatar.builder()
                .imageType(avatar.getContentType())
                .data(avatar.getBytes())
                .product(product)
                .build();

        product.setProductAvatar(productAvatar);

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
        LocalDateTime now = LocalDateTime.now();

        List<ProductResponse> productResponses = products.getContent()
                .stream()
                .map(product -> {
                    // Chuyển đổi Product sang ProductResponse
                    ProductResponse productResponse = productMapper.toProductResponse(product);

                    // Kiểm tra nếu danh sách variant không rỗng
                    if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                        // Lấy sản phẩm variant đầu tiên (hoặc chọn variant theo logic của bạn)
                        ProductVariant variant = product.getVariants().get(0);

                        // Lấy discount nếu có theo variant và thời gian hiện tại
                        Optional<Discount> discountOptional = discountRepository
                                .findFirstByProductVariant_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                                        variant.getId(), now, now);

                        // Nếu có discount thì lấy giá trị, nếu không thì mặc định là 0
                        int discountValue = discountOptional.map(Discount::getDiscountValue).orElse(0);

                        // Gán giá trị discount vào trường discountDisplayed của ProductResponse
                        productResponse.setDiscountDisplayed(discountValue);
                    } else {
                        // Nếu không có variant, mặc định discount là 0
                        productResponse.setDiscountDisplayed(0);
                    }

                    return productResponse;
                })
                .collect(Collectors.toList());

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

        LocalDateTime now = LocalDateTime.now();

        List<ProductResponse> productResponses = products.getContent()
                .stream()
                .map(product -> {
                    // Chuyển đổi Product sang ProductResponse
                    ProductResponse productResponse = productMapper.toProductResponse(product);

                    // Kiểm tra nếu danh sách variant không rỗng
                    if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                        // Lấy sản phẩm variant đầu tiên (hoặc chọn variant theo logic của bạn)
                        ProductVariant variant = product.getVariants().get(0);

                        // Lấy discount nếu có theo variant và thời gian hiện tại
                        Optional<Discount> discountOptional = discountRepository
                                .findFirstByProductVariant_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                                        variant.getId(), now, now);

                        // Nếu có discount thì lấy giá trị, nếu không thì mặc định là 0
                        int discountValue = discountOptional.map(Discount::getDiscountValue).orElse(0);

                        // Gán giá trị discount vào trường discountDisplayed của ProductResponse
                        productResponse.setDiscountDisplayed(discountValue);
                    } else {
                        // Nếu không có variant, mặc định discount là 0
                        productResponse.setDiscountDisplayed(0);
                    }

                    return productResponse;
                })
                .collect(Collectors.toList());

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

        LocalDateTime now = LocalDateTime.now();

        List<ProductResponse> productResponses = products.getContent()
                .stream()
                .map(product -> {
                    // Chuyển đổi Product sang ProductResponse
                    ProductResponse productResponse = productMapper.toProductResponse(product);

                    // Kiểm tra nếu danh sách variant không rỗng
                    if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                        // Lấy sản phẩm variant đầu tiên (hoặc chọn variant theo logic của bạn)
                        ProductVariant variant = product.getVariants().get(0);

                        // Lấy discount nếu có theo variant và thời gian hiện tại
                        Optional<Discount> discountOptional = discountRepository
                                .findFirstByProductVariant_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                                        variant.getId(), now, now);

                        // Nếu có discount thì lấy giá trị, nếu không thì mặc định là 0
                        int discountValue = discountOptional.map(Discount::getDiscountValue).orElse(0);

                        // Gán giá trị discount vào trường discountDisplayed của ProductResponse
                        productResponse.setDiscountDisplayed(discountValue);
                    } else {
                        // Nếu không có variant, mặc định discount là 0
                        productResponse.setDiscountDisplayed(0);
                    }

                    return productResponse;
                })
                .collect(Collectors.toList());

        return CustomPageResponse.<ProductResponse>builder()
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .content(productResponses)
                .build();
    }


    public ProductDetailResponse getById(String id) {
        return productMapper.toProductDetailResponse(productRepository.findById(id)
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
    public Long createProductVariant(CreateVariantRequest request, List<MultipartFile> files) throws IOException {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist", "product-e-01"));

        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "At least one image is required", "variant-e-01");
        }

        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .price(request.getPrice())
                .color(request.getColor())
                .discount(0)
                .colorCode(request.getColorCode())
                .isActive(false)
                .sold(0)
                .stock(0)
                .variantImages(new ArrayList<>())
                .build();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                VariantImage variantImage = VariantImage.builder()
                        .imageType(file.getContentType())
                        .data(file.getBytes())
                        .productVariant(productVariant)
                        .build();

                productVariant.getVariantImages().add(variantImage);
            }
        }

        productVariantRepository.save(productVariant);

        PriceHistory newPriceHistory = PriceHistory.builder()
                .productVariant(productVariant)
                .price(productVariant.getPrice())
                .startDate(LocalDateTime.now())
                .endDate(null)
                .build();
        priceHistoryRepository.save(newPriceHistory);

        return productVariant.getId();
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String updateProductVariant(Long id, UpdateVariantRequest request, List<MultipartFile> newImages) throws IOException {
        // Lấy thông tin ProductVariant hiện tại
        ProductVariant productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Variant does not exist", "variant-e-01"));

        // Kiểm tra xem giá có thay đổi không
        boolean priceChanged = !productVariant.getPrice().equals(request.getPrice());

        // Cập nhật các trường không liên quan đến giá
        productVariant.setColor(request.getColor());
        productVariant.setIsActive(request.getIsActive());
        productVariant.setColorCode(request.getColorCode());

        //Xóa các ảnh có trong danh sách removeImageIds
        if (request.getRemoveImageIds() != null && !request.getRemoveImageIds().isEmpty()) {
            Iterator<VariantImage> iterator = productVariant.getVariantImages().iterator();
            while (iterator.hasNext()) {
                VariantImage image = iterator.next();
                if (request.getRemoveImageIds().contains(image.getId())) {
                    iterator.remove(); // Xoá trực tiếp trong list gốc
                }
            }
        }

        if(newImages != null && !newImages.isEmpty()){
            for(MultipartFile file : newImages){
                if (!file.isEmpty()) {
                    VariantImage image = VariantImage.builder()
                            .imageType(file.getContentType())
                            .data(file.getBytes())
                            .productVariant(productVariant)
                            .build();
                    productVariant.getVariantImages().add(image);
                }
            }
        }

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

    public List<PriceResponse> getAllPriceHistoryByVariant(Long variantId){
        List<PriceHistory> priceHistories = priceHistoryRepository.findByProductVariant_Id(variantId);
        return priceHistories.stream()
                .map(priceMapper::toPriceResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);

        // Sử dụng MapStruct để chuyển đổi danh sách Product -> ProductResponse
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getRelatedProducts(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product not found", "product-e-01"));

        if (product.getRelated_id() == null || product.getRelated_id().isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> relatedProducts = productRepository.findAllByIdIn(product.getRelated_id());

        log.info(relatedProducts.toString());
        log.info(product.getRelated_id().toString());

        return relatedProducts.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public CustomPageResponse<ProductResponse> filterProducts(
            String categoryId,
            ProductStatus status,
            String sortBy,
            String sortDirection,
            Pageable pageable
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Product> products;
        if (categoryId != null && status != null) {
            products =  productRepository.findByStatusAndCategoryId(status, categoryId, sortedPageable);
        } else if (categoryId != null) {
            products =  productRepository.findByCategoryId(categoryId, sortedPageable);
        } else if (status != null) {
            products =  productRepository.findByStatus(status, sortedPageable);
        } else {
            products =  productRepository.findAll(sortedPageable);
        }
        // Thêm logic convert sang ProductResponse
        LocalDateTime now = LocalDateTime.now();
        List<ProductResponse> content = products.getContent().stream()
                .map(product -> {
                    ProductResponse response = productMapper.toProductResponse(product);

                    if (!product.getVariants().isEmpty()) {
                        ProductVariant variant = product.getVariants().get(0);
                        Optional<Discount> discount = discountRepository.findFirstByProductVariant_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                                variant.getId(), now, now
                        );
                        response.setDiscountDisplayed(discount.map(Discount::getDiscountValue).orElse(0));
                    }

                    return response;
                })
                .collect(Collectors.toList());

        return new CustomPageResponse<>(
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                content
        );
    }

}

//
//        if (product.getImages().isEmpty()) {
//            throw new AppException(HttpStatus.BAD_REQUEST, "Product must have at least one image", "product-e-02");
//        }
//
//        if(files != null && !files.isEmpty()){
//            for(MultipartFile file : files){
//                if (!file.isEmpty()) {
//                    Image image = Image.builder()
//                            .imageType(file.getContentType())
//                            .data(file.getBytes())
//                            .product(product)
//                            .build();
//                    product.getImages().add(image);
//                }
//            }
//        }