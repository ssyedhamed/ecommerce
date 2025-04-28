package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.exceptions.APIException;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.CartItem;
import com.syedhamed.ecommerce.model.Category;
import com.syedhamed.ecommerce.model.Product;
import com.syedhamed.ecommerce.payload.ProductDTO;
import com.syedhamed.ecommerce.payload.ProductResponse;
import com.syedhamed.ecommerce.repository.CartItemRepository;
import com.syedhamed.ecommerce.repository.CategoryRepository;
import com.syedhamed.ecommerce.repository.ProductRepository;
import com.syedhamed.ecommerce.service.contract.FileService;
import com.syedhamed.ecommerce.service.contract.PaginationService;
import com.syedhamed.ecommerce.service.contract.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final PaginationService paginationService;
    private final CartItemRepository cartItemRepository;

    @Value("${default.image.product}")
    private String DEFAULT_PRODUCT_IMAGE;

    @PreAuthorize("hasRole('ROLE_SELLER')")

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        //check if the product already exists with the same name
        boolean productExists = productRepository.existsByProductNameIgnoreCaseAndCategory_CategoryId(productDTO.getProductName(), categoryId);
        if (productExists) {
            throw new APIException("Product already exists in the chosen category");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "id", categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
//        category.getProductSet().add(product); //one way
        product.setCategory(category); //other way

        product.setProductImage(DEFAULT_PRODUCT_IMAGE);

        if (product.getDiscount() != null && product.getDiscount() > 0) {
            double specialPrice = product.getPrice() -
                    ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);

        }
        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProductsByKeyword(String keyword,
                                                   Integer pageNumber,
                                                   Integer pageSize,
                                                   String sortBy,
                                                   String sortOrder) {
        Page<Product> productsPageData;
        Pageable pageDetails = paginationService.getPageableObject(pageNumber, pageSize, sortBy, sortOrder);

        if (keyword != null && !keyword.isEmpty()) {
            //this will retrieve the products by partially matching the keyword
            //if Like is used instead of containing, the keyword should be wrapped
            // with wildcards (%keyword%)
            productsPageData = productRepository.findByProductNameContainingIgnoreCase(keyword, pageDetails);
        } else {
            //this will return all the products
            productsPageData = productRepository.findAll(pageDetails);
        }

        return paginationService.getPaginatedResponse(productsPageData, ProductDTO.class, ProductResponse.class);
    }


    @Override
    public ProductResponse getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Optional<Category> catOpt = categoryRepository.findById(categoryId);
        if (catOpt.isEmpty()) {
            throw new ResourceNotFoundException("Category", "Id:", categoryId);
        }
        Pageable pageDetails = paginationService.getPageableObject(pageNumber, pageSize, sortBy, sortOrder);

        Page<Product> productsPageData = productRepository.findByCategory_categoryId(categoryId, pageDetails);
        return paginationService.getPaginatedResponse(productsPageData, ProductDTO.class, ProductResponse.class);
    }


    @PreAuthorize("hasRole('ROLE_SELLER')")

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        log.info("Updating product...");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        log.info("Product found by id: [{}]", productId);
        log.info("Current product's special price: [{}]", product.getSpecialPrice());
        double updatedSpecialPrice = product.getSpecialPrice();
        // Map the fields from ProductDTO to Product entity
        // Only non-null fields in productDTO will overwrite the corresponding fields in product
        modelMapper.map(productDTO, product);
        // Recalculate the special price if discount is available
        log.info("Recalculating special price...");
        log.info("Current special price: [{}]", updatedSpecialPrice);
        if (product.getDiscount() != null && product.getDiscount() > 0) {
            updatedSpecialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(updatedSpecialPrice);
        }
        log.info("Updated special price: [{}]", product.getSpecialPrice());

        // the product exists in the cart -> then the product is updated, -> should reflect the changes of special price of product in the cart
        List<CartItem> cartItems = cartItemRepository.findByProduct(product);
        for(CartItem item : cartItems){
            log.info("[{}]'s product special price is updated from [{}] to [{}]", item.getClass(), item.getProduct().getSpecialPrice(), updatedSpecialPrice);
            item.setProduct(product);
        }
        log.info("Current cart Item product : [{}]", cartItems.get(0).getProduct());
        //saving the product after updating
        Product save = productRepository.save(product);
        log.info("updated product: [{}]", save);
        return modelMapper.map(save, ProductDTO.class);
    }

    @PreAuthorize("hasAnyRole('SELLER, ADMIN')")
    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        productRepository.delete(product);

    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @Override
    public void setImage(Long productId, String fileName) {
        Product product = getProductById(productId);
        product.setProductImage(fileName);

        productRepository.save(product);
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Override
    public void deleteImage(Product product, Boolean isDeleted) {
        if (isDeleted) {
            product.setProductImage(fileService.getImageName());
            productRepository.save(product);
        } else {
            throw new ResourceNotFoundException("Image", "name", product.getProductImage());
        }
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", productId));

    }

    @Override
    public List<ProductDTO> getAllProducts() {

        return productRepository.findAll().stream().map(
                product -> modelMapper.map(product, ProductDTO.class)
        ).toList();

    }
}
