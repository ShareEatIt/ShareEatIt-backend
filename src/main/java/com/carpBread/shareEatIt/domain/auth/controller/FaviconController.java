package com.carpBread.shareEatIt.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    @GetMapping("/favicon.ico")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void favicon(){
        System.out.println("FaviconController.favicon");
        return ;
    }
}
