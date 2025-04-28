package com.syedhamed.ecommerce.service.contract;

import com.syedhamed.ecommerce.payload.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaginationService {
    Pageable getPageableObject(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    //Page<E> pageData - Page object of Entity containing all the entities in a Page (repo.findBy(Pageable object))
    /*
    Purpose: [Dynamic model mapping and response type]
    1. Converts the List of entities (content of the Pageable object) to DTOs using modelmapper and dtoClass
    2. Creates the instance of responseType (productResponse or categoryResponse or userResponse)
     using responseType.getDeclaredConstructor().newInstance();
    3. Sets the content and other fields (page number etc.) of the responseType from the pageData
    4. returns the responseType
     */
    <T, E, R extends PaginatedResponse<T>> R getPaginatedResponse(Page<E> pageData,
                                                                  Class<T> dtoClass, Class<R> responseType);
}