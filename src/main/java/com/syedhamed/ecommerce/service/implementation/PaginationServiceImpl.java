package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.payload.PaginatedResponse;
import com.syedhamed.ecommerce.service.contract.PaginationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaginationServiceImpl implements PaginationService {


    private final ModelMapper modelMapper;

    public Pageable getPageableObject(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    /**
     * A generic method to convert paginated entity data into a paginated response DTO.
     *
     * @param <T> The DTO type (e.g., ProductDTO, CategoryDTO).
     * @param <E> The entity type (e.g., Product, Category).
     * @param <R> The response type, extending {@link PaginatedResponse<T>}.
     * @param pageData The {@link Page} object containing paginated entity data.
     * @param dtoClass The class type of the DTO.
     * @param responseType The class type of the response extending {@link PaginatedResponse<T>}.
     * @return A paginated response object of type {@code R}.
     * @throws RuntimeException If response creation fails.
     * <p>Internally, this method extracts the list of entities from the {@code pageData}
     * and stores it in a local variable called {@code entities} for processing.</p>
     */
    public <T, E, R extends PaginatedResponse<T>> R getPaginatedResponse(Page<E> pageData, 
                                                                         Class<T> dtoClass, Class<R> responseType){
        //Fetch the content (entities) of the Page
        List<E> entities = pageData.getContent();
        // convert entities to dtos as response's content field is of type DTO
        List<T> content =  entities.stream().map( entity -> modelMapper.map(entity, dtoClass)).toList();
        try {
        //create the instance type (ProductResponse or CategoryResponse) using responseType
            R response = responseType.getDeclaredConstructor().newInstance();
            //set the fields of response with the given one
            response.setContent(content);
            response.setPageNumber(pageData.getNumber());
            response.setTotalPages(pageData.getTotalPages());
            response.setTotalElements(pageData.getTotalElements());
            response.setPageSize(pageData.getSize());
            response.setLastPage(pageData.isLast());
            //return the paginated response of given type (ProductResponse or CategoryResponse)
            return response;
            // newInstance() method can throw the following exceptions
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }




    //Deprecated ⬇️

//
//    public ProductResponse getProductResponse(Page<Product> productsPage, List<Product> products) {
//        List<ProductDTO> content = products.stream()
//                .map(product ->
//                        modelMapper.map(product, ProductDTO.class)).toList();
//        ProductResponse response = new ProductResponse();
//        response.setContent(content);
//        response.setPageNumber(productsPage.getNumber());
//        response.setPageSize(productsPage.getSize());
//        response.setTotalElements(productsPage.getTotalElements());
//        response.setTotalPages(productsPage.getTotalPages());
//        response.setLastPage(productsPage.isLast());
//        return response;
//    }

//    // E -> entity and T -> DTO
//    public <T, E>PaginatedResponse<T> getPaginatedResponse( Page<E> pageData, List<E> entities, Class<T> dtoClass){
//        List<T> content = entities.stream().map(entity -> modelMapper.map(entity, dtoClass)).toList();
//        return new PaginatedResponse<T>(
//                content,
//                pageData.getNumber(),
//                pageData.getSize(),
//                pageData.getTotalElements(),
//                pageData.getTotalPages(),
//                pageData.isLast()
//        );
//    }

}
