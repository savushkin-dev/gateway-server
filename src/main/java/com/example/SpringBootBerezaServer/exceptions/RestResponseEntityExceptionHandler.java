package com.example.SpringBootBerezaServer.exceptions;

import com.example.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.example.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import com.example.SpringBootBerezaServer.model.AppError;
import com.example.SpringBootBerezaServer.model.Sysstat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AuthenticationException.class}) //отвечает за авторизацию
    public ResponseEntity<AppError> handleAuthenticationException(Exception ex, HttpServletRequest request, HttpServletResponse response) {

        AppError error = new AppError(ex.getMessage());
        if (response.getHeader("error") != null)
            error.setMessage(response.getHeader("error"));

        return ResponseEntity.status(response.getStatus())
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .body(error);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<AppError> handleAccessDeniedException(Exception ex, WebRequest request) {
        AppError response = new AppError("AccessDeniedException");
        return new ResponseEntity<AppError>(
                response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<AppError> handleUserOrgNotFoundException(Exception ex, WebRequest request) {

        AppError response = new AppError("UserOrgNotFoundException");
        return new ResponseEntity<AppError>(
                response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<AppError> handleBadCredentialsException(Exception ex, WebRequest request) {

        AppError response = new AppError("BadCredentialsException; ");
        return new ResponseEntity<AppError>(
                response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserNotCreatedException.class})
    public ResponseEntity<AppError> handleUserOrgNotCreatedException(Exception ex, WebRequest request) {

        AppError response = new AppError("UserOrgNotCreatedException; " + ex.getMessage());
        return new ResponseEntity<AppError>(
                response, HttpStatus.UNAUTHORIZED);
    }

    //    @RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
    @ExceptionHandler({UserNotUpdatedException.class})
    public ResponseEntity<AppError> handleUserOrgNotUpdatedException(Exception ex, WebRequest request) {

        AppError response = new AppError("UserOrgNotUpdatedException; " + ex.getMessage());
        return new ResponseEntity<AppError>(
                response, HttpStatus.BAD_REQUEST);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_XML).body(response);
    }

    @ExceptionHandler({XMLParsingException.class})
    public ResponseEntity<Sysstat> handleXMLParsingException(Exception ex, WebRequest request) {

        Sysstat response = new Sysstat(HttpStatus.BAD_REQUEST.value(), "XMLParsingException: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_XML).body(response);
    }


//    @RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
    @ExceptionHandler({NasException.class})
    public ResponseEntity<?> handleNasException(Exception ex, WebRequest request) {
        AppError response = new AppError("NasException; " + ex.getMessage());
//        return new ResponseEntity<AppError>(
//                response, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_XML).body(response);
    }

    @ExceptionHandler({NasRemoteServerException.class})
    public ResponseEntity<?> handleNasRemoteServerException(Exception ex, WebRequest request) {
        Sysstat response = new Sysstat(HttpStatus.BAD_REQUEST.value(), "NasRemoteServerException: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_XML).body(response);
    }

//    @ExceptionHandler({Exception.class}) //отвечает за авторизацию
//    public ResponseEntity<AppError> handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
//
//        System.out.println("Handler response!");
//
//        AppError error = new AppError(ex.getMessage());
//        if (response.getHeader("error") != null)
//            error.setMessage(response.getHeader("error"));
//
//        return ResponseEntity.status(response.getStatus())
//                .contentType(MediaType.parseMediaType(response.getContentType()))
//                .body(error);
//    }
}
