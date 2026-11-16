package org.gestion.gestionstock.handlers;

import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNoFoundException(EntityNotFoundException exception, WebRequest webRequest){

        final HttpStatus notfound = HttpStatus.NOT_FOUND;
        final ErrorDto errorDto= ErrorDto.builder()
                .code(exception.getErrorCodes())
                .httpCode(notfound.value())
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(errorDto, notfound);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorDto> handleInvalidOperationException(InvalidOperationException exception, WebRequest webRequest){

        final HttpStatus notfound = HttpStatus.BAD_REQUEST;
        final ErrorDto errorDto= ErrorDto.builder()
                .code(exception.getErrorCodes())
                .httpCode(notfound.value())
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(errorDto, notfound);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<ErrorDto> handleInvalidEntityException(InvalidEntityException exception, WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        final ErrorDto errorDto = ErrorDto.builder()
                .code(exception.getErrorCodes())
                .httpCode(badRequest.value())
                .message(exception.getMessage())
                .errors(exception.getErrors())
                .build();

        return new ResponseEntity<>(errorDto, badRequest);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto> handleBadCredentialsException(BadCredentialsException exception, WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        final ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCodes.BAD_CREDENTIALS)
                .httpCode(badRequest.value())
                .message(exception.getMessage())
                .errors(Collections.singletonList("Login et / ou mot de passe incorrecte"))
                .build();

        return new ResponseEntity<>(errorDto, badRequest);
    }

    @ExceptionHandler({org.springframework.security.authentication.DisabledException.class,
            org.springframework.security.authentication.LockedException.class,
            org.springframework.security.authentication.AccountExpiredException.class,
            org.springframework.security.authentication.CredentialsExpiredException.class})
    public ResponseEntity<ErrorDto> handleAccountStatusException(org.springframework.security.core.AuthenticationException exception, WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        final ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCodes.BAD_CREDENTIALS)
                .httpCode(badRequest.value())
                .message("Ce compte est désactivé. Contactez votre administrateur.")
                .errors(Collections.singletonList("Ce compte est désactivé. Contactez votre administrateur."))
                .build();
        return new ResponseEntity<>(errorDto, badRequest);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException exception, WebRequest webRequest){
        final HttpStatus conflict = HttpStatus.CONFLICT;
        final ErrorDto errorDto = ErrorDto.builder()
                .httpCode(conflict.value())
                .message("Opération impossible : cet élément est lié à d'autres données.")
                .errors(Collections.singletonList("Opération impossible : cet élément est lié à d'autres données."))
                .build();
        return new ResponseEntity<>(errorDto, conflict);
    }
}
