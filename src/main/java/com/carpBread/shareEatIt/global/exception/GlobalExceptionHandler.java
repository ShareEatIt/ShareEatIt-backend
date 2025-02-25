package com.carpBread.shareEatIt.global.exception;

import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import io.sentry.spring.jakarta.EnableSentry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.DateTimeException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeoutException;

import static org.springframework.http.HttpStatus.*;

/* 어플리케이션 내 발생하는 런타임 예외 핸들러 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${sentry.dsn}")
    private String dsn;

//    @PostConstruct
//    public void init() {
//        // Sentry 초기화 설정
//        Sentry.init(options -> {
//            options.setDsn(dsn);
//            options.setBeforeSend((event, hint) -> {
//                System.out.println("Sentry Event: " + event.getExtras());
//                return event;
//            });
//        });
//
//        if (Sentry.isEnabled()) {
//            log.info("✅ Sentry initialized with DSN: {}", dsn);
//        } else {
//            log.error("❌ Sentry 초기화 실패! DSN 확인 필요");
//        }
//        System.out.println("Sentry initialized");
//    }

    /* CustomException 핸들러 */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomExceptionResponseDto> handleAppException(CustomException e, HttpServletRequest request){

        // Sentry 초기화 설정
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setTracesSampleRate(1.0);  // 샘플링 비율 설정
        });

        // response dto 생성
        CustomExceptionResponseDto responseDto= new CustomExceptionResponseDto(e);

        // sentry에 전송할 내용
        Sentry.configureScope(scope ->{
            scope.setTransaction(request.getMethod() + " " + request.getRequestURI());
            scope.setExtra("file_path", responseDto.getFilePath());
            scope.setExtra("exception_status", responseDto.getExceptionStatus());
            scope.setExtra("message", responseDto.getMessage());
            scope.setExtra("timestamp", String.valueOf(responseDto.getTimestamp()));
            scope.setExtra("causation", responseDto.getCausation());
            scope.setExtra("request_method", request.getMethod());
            scope.setExtra("request_uri", request.getRequestURI());
            scope.setTag("tag", responseDto.getTag());
            Sentry.captureException(e);
        });



        System.out.println("커스텀 핸들러 실행됨");

        // controller에서와 달리 exception 발생 시에는 각 HttpStatus가 다르므로, status() 함수 사용을 위해 ResponseEntity를 사용하였습니다.
        return ResponseEntity.status(e.getExceptionStatus().getStatus()).body(responseDto);
    }

    /*-----------------------------------------------------------------------------------------------------*/

    /**
     * 메서드 주석: 상태코드 - 상태코드명 - {예외클래스명}
     * {예외클래스명}이 없는 경우 - 여러 예외 클래스 공통 메서드
     * {예외클래스명}이 있는 경우 - 특정 예외 클래스 커스텀 메서드
     */

    /* 400 Bad Request 핸들러 */
    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IndexOutOfBoundsException.class,
            MissingServletRequestParameterException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class, DataIntegrityViolationException.class, DateTimeException.class})
    public ResponseEntity<UncheckedExceptionResponseDto> handleBadRequestException(RuntimeException e, HttpServletRequest request) {
        // responseDto 생성
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(BAD_REQUEST).body(responseDto);
    }

    /* 400 Bad Request  - MethodArgumentTypeMismatchException 핸들러 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<UncheckedExceptionResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        // responseDto 생성
        String errorMessage = String.format(
                "요청한 파라미터 '%s'에 잘못된 값 '%s'이(가) 전달되었습니다.", e.getName(), e.getValue());
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e, errorMessage);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(BAD_REQUEST).body(responseDto);
    }

    /* 401 Unauthorized 핸들러 */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<UncheckedExceptionResponseDto> handleUnauthorizedException(RuntimeException e, HttpServletRequest request) {
        // responseDto 생성
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(UNAUTHORIZED).body(responseDto);
    }

    /* 403 Forbidden 핸들러 */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<UncheckedExceptionResponseDto> handleForbiddenException(RuntimeException e, HttpServletRequest request) {
        // responseDto 생성
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(FORBIDDEN).body(responseDto);
    }

    /* 405 Method Not Allowed -  HttpRequestMethodNotSupportedException 핸들러 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<UncheckedExceptionResponseDto> handleHttpRequestMethodNotSupportedException(RuntimeException e, HttpServletRequest request) {
        // responseDto 생성
        String errorMessage = String.format(
                "지원되지 않는 HTTP 메서드입니다. 요청된 메서드: %s", request.getMethod());
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e, errorMessage);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(responseDto);
    }

    /* 408  Request Timeout - TimeoutException 핸들러 */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<UncheckedExceptionResponseDto> handleTimeoutException(RuntimeException e, HttpServletRequest request) {
        // responseDto 생성
        String errorMessage = "시스템 작업을 시간을 초과했습니다.";
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e, errorMessage);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(REQUEST_TIMEOUT).body(responseDto);
    }

    /* 409 Conflict- ConcurrentModificationException 핸들러 */
    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<UncheckedExceptionResponseDto> handleConcurrentModificationException(RuntimeException e, HttpServletRequest request) {
        // responseDto 생성
        String errorMessage = "동시 처리 오류가 발생했습니다. ";
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e, errorMessage);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(CONFLICT).body(responseDto);
    }

    /* 500  Internal Server Error 핸들러 */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<UncheckedExceptionResponseDto> handleInternalServerErrorException(RuntimeException e, HttpServletRequest request) {
        // responseDto 생성
        String errorMessage = "서버에러 - 시스템에서 예기치 않은 오류가 발생했습니다";
        UncheckedExceptionResponseDto responseDto = new UncheckedExceptionResponseDto(e, errorMessage);

        // sentry에 전송할 내용 추가
        sentryConfigure(e, request, responseDto);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(responseDto);
    }

    /*---------------------------------------------------------------------------------------------*/

    // sentry 전달 내용 설정 메서드
    private void sentryConfigure(RuntimeException e, HttpServletRequest request, UncheckedExceptionResponseDto responseDto) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        StackTraceElement[] stackTrace = e.getStackTrace();
        StackTraceElement element = stackTrace.length > 0 ? stackTrace[0] : null;  // 예외가 없을 경우 대비

        // Sentry 초기화 설정
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setTracesSampleRate(1.0);  // 샘플링 비율 설정
        });

        // sentry에 전송할 내용
        Sentry.withScope(scope -> {
            scope.setTransaction(method + requestURI);
            // 추가 정보
            scope.setExtra("file_path", element.getClassName());
            scope.setExtra("exception_status", responseDto.getExceptionStatus());
            scope.setExtra("message", responseDto.getMessage());
            scope.setExtra("timestamp", String.valueOf(responseDto.getTimestamp()));
            scope.setExtra("causation", responseDto.getCause());
            scope.setExtra("request_method", method);
            scope.setExtra("request_uri", requestURI);
            // 예외를 sentry로 전송
            Sentry.captureException(e);
        });

    }
}

