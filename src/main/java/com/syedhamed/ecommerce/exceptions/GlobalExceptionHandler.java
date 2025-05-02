package com.syedhamed.ecommerce.exceptions;

import com.syedhamed.ecommerce.payload.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final APIResponse apiResponse;
    /**
     * Thrown when a request body annotated with @Valid fails validation.
     * Example: A required field is null, a string is too short, etc.
     * Triggered by: Spring validation framework (Hibernate Validator, Bean Validation API).
     * Thrown from: Controller layer, when binding the request body to a DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<APIResponse<Map<String, String>>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err ->{
            String field = (err instanceof FieldError) ? ((FieldError) err).getField() : err.getObjectName();
            String message = err.getDefaultMessage();
            errors.put(field, message);
        });
        APIResponse<Map<String, String>> response = new APIResponse<>(
                errors,
                "Validation failed",
                false
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Thrown when an entity is not found in the database.
     * Example: Trying to fetch a product/user/order with a non-existent ID.
     * Triggered by: Your service layer when doing repo.findById().orElseThrow().
     * Thrown from: Custom code using `throw new ResourceNotFoundException(...)`.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundExceptionHandler(ResourceNotFoundException e){
        String message = e.getMessage();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleInvalidJson(HttpMessageNotReadableException ex, WebRequest request) {
        String rawMessage = ex.getMostSpecificCause().getMessage();
        String sanitizedMessage = rawMessage != null
                ? rawMessage.split("\n")[0] // Just keep the first line
                : "Malformed JSON or bad input data";

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        Map<String, String> errorDetails = new LinkedHashMap<>();
        errorDetails.put("error", "Invalid JSON format");
        errorDetails.put("reason", sanitizedMessage);
        errorDetails.put("path", path);

        return ResponseEntity.badRequest().body(
                new APIResponse<>(errorDetails, "Bad Request", false)
        );
    }



    /**
     * Thrown when the type of a request parameter or path variable does not match the expected type.
     * Example: Passing a string instead of a number in the URL (e.g., /api/product/abc where id is expected to be Long).
     * Triggered by: Spring MVC automatically when binding path/query params.
     * Thrown from: Controller layer during parameter binding.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse> myMethodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e){
        String message = e.getMessage();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Thrown manually when a business rule is violated.
     * Example: Trying to add more items to cart than available stock.
     * Triggered by: You — inside service methods when enforcing app-specific logic.
     * Thrown from: `throw new APIException("message")` in your own code.
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIExceptionHandler(APIException e){
        String message = e.getMessage();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);
        apiResponse.setData(null);
        return  new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Thrown when an invalid property is used in Spring Data queries.
     * Example: Client tries to sort by "prize" instead of "price".
     * Triggered by: Spring Data JPA during query building.
     * Thrown from: Repositories when invalid property names are passed in findAll(Pageable).
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public  ResponseEntity<APIResponse> PropertyReferenceException(PropertyReferenceException e){
        String message = e.getMessage();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse> badCredentialsException(BadCredentialsException e){
        String message = e.getMessage();
        apiResponse.setMessage(message);
        apiResponse.setStatus(false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
