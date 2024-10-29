package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
    @GetMapping("/api/csrf-token")
//    public CsrfToken csrfToken(HttpServletRequest request) {
//        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//    }
    public String sayHello(HttpServletRequest request) {
        return "HEllo World";
    }
}

