package com.carpBread.shareEatIt.domain.auth.controller;

import com.carpBread.shareEatIt.domain.auth.dto.response.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.service.Pre_OAuth2Service;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/* 소셜 로그인 controller */
// 💡 참고 : 로그인/로그아웃 관련하여 변경사항이 많아 수정된 내용이 많아 코드만 남겨둔 상태. 프런트와 안정적인 연결 후 삭제 예정임.
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/authorize")
@Slf4j
public class Pre_OAuth2LoginController {

    private final Pre_OAuth2Service preOAuth2Service;

    @GetMapping
    public ResponseEntity<ApiResponse<AuthLoginResponseDto>> oauth2Login(@RequestParam(name = "code")String code){
        String oauth2AccessToken="";
        AuthLoginResponseDto responseDto=null;

        try{
            oauth2AccessToken= preOAuth2Service.getAccessOAuth2Token(code);
            responseDto= preOAuth2Service.getMemberInfo(oauth2AccessToken);
        }catch (Exception e){
            throw new CustomException(
                    CustomExceptionStatus.LOGIN_FAIL,
                    "소셜 로그인에 실패하였습니다. \n Error Message : "+e.getMessage(),
                    Pre_OAuth2LoginController.class.getName(),
                    null,
                    Domain.AUTH
            );
        }

        ApiResponse<AuthLoginResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "소셜 로그인 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }
}
