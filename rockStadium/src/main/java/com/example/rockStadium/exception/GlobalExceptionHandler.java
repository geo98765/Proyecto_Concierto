// package com.example.rockStadium.exception;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;

// import lombok.extern.slf4j.Slf4j;

// /**
//  * Global exception handler for all REST controllers
//  * Centralizes error handling and provides consistent error responses
//  * 
//  * Note: Using @ControllerAdvice + @ResponseBody instead of @RestControllerAdvice
//  * to avoid compatibility issues with SpringDoc 2.5.0
//  */
// //@ControllerAdvice
// //@ResponseBody
// @Slf4j
// public class GlobalExceptionHandler {
    
//     /**
//      * Handle ResourceNotFoundException (404)
//      * Triggered when a requested resource is not found in the database
//      */
//     @ExceptionHandler(ResourceNotFoundException.class)
//     public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
//         log.error("Resource not found: {}", ex.getMessage());
        
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.NOT_FOUND.value())
//                 .error("Not Found")
//                 .message(ex.getMessage())
//                 .build();
        
//         return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//     }
    
//     /**
//      * Handle BusinessRuleException (400)
//      * Triggered when a business rule is violated
//      */
//     @ExceptionHandler(BusinessRuleException.class)
//     public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(BusinessRuleException ex) {
//         log.error("Business rule violation: {}", ex.getMessage());
        
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.BAD_REQUEST.value())
//                 .error("Bad Request")
//                 .message(ex.getMessage())
//                 .build();
        
//         return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//     }
    
//     /**
//      * Handle validation errors (400)
//      * Triggered when @Valid annotation fails on request DTOs
//      */
//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
//         log.error("Validation error: {}", ex.getMessage());
        
//         Map<String, String> errors = new HashMap<>();
//         ex.getBindingResult().getAllErrors().forEach(error -> {
//             String fieldName = ((FieldError) error).getField();
//             String errorMessage = error.getDefaultMessage();
//             errors.put(fieldName, errorMessage);
//         });
        
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.BAD_REQUEST.value())
//                 .error("Validation Failed")
//                 .message("Invalid input parameters")
//                 .validationErrors(errors)
//                 .build();
        
//         return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//     }
    
//     /**
//      * Handle all other unexpected exceptions (500)
//      * Catch-all for any unhandled exceptions
//      */
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//         log.error("Unexpected error occurred", ex);
        
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                 .error("Internal Server Error")
//                 .message("An unexpected error occurred. Please try again later.")
//                 .build();
        
//         return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//     }
// }