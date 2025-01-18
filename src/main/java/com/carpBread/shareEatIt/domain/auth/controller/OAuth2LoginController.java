package com.carpBread.shareEatIt.domain.auth.controller;

import com.carpBread.shareEatIt.domain.auth.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.service.OAuth2Service;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/authorize")
@Slf4j
public class OAuth2LoginController {

    private final OAuth2Service oAuth2Service;

    @GetMapping
    public ResponseEntity<ApiResponse<AuthLoginResponseDto>> oauth2Login(@RequestParam(name = "code")String code){
        String oauth2AccessToken="";
        AuthLoginResponseDto responseDto=null;

        try{
            oauth2AccessToken=oAuth2Service.getAccessOAuth2Token(code);
            responseDto=oAuth2Service.getMemberInfo(oauth2AccessToken);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new AppException(ErrorCode.LOGOUT_FAIL,e.getMessage(),"/oauth2/authorize");
        }

        ApiResponse<AuthLoginResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "소셜 로그인 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }
}
