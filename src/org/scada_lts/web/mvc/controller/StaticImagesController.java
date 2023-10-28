package org.scada_lts.web.mvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import static org.scada_lts.utils.StaticImagesUtils.getAndSendImage;

@RestController
public class StaticImagesController {

    @GetMapping(value = "/graphics/**")
    public ResponseEntity<String> graphics(HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(request, response);
    }

    @GetMapping(value = "/uploads/**")
    public ResponseEntity<String> uploads(HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(request, response);
    }

    @GetMapping(value = "/images/**")
    public ResponseEntity<String> images(HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(request, response);
    }

    @GetMapping(value = "/img/**")
    public ResponseEntity<String> img(HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(request, response);
    }

    @GetMapping(value = "/assets/**")
    public ResponseEntity<String> assets(HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(request, response);
    }

    @GetMapping(value = "/resources/**")
    public ResponseEntity<String> resources(HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(request, response);
    }

    @GetMapping(value = "/static/**")
    public ResponseEntity<String> stat(HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(request, response);
    }
}