package org.scada_lts.web.mvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.File;

import static org.scada_lts.utils.StaticImagesUtils.getAndSendImage;

@RestController
public class StaticImagesController {

    @GetMapping(value = "/graphics/{folder}/{fileName}.{fileExt}")
    public ResponseEntity<String> graphics(@PathVariable("folder") String folder,
                                           @PathVariable("fileName") String fileName,
                                           @PathVariable("fileExt") String fileExt,
                                           HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(folder + File.separator + fileName + "." + fileExt, request, response);
    }

    @GetMapping(value = "/uploads/{fileName}.{fileExt}")
    public ResponseEntity<String> uploads(@PathVariable("fileName") String fileName,
                                          @PathVariable("fileExt") String fileExt,
                                          HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(fileName + "." + fileExt, request, response);
    }

    @GetMapping(value = "/images/{fileName}.{fileExt}")
    public ResponseEntity<String> images(@PathVariable("fileName") String fileName,
                                         @PathVariable("fileExt") String fileExt,
                                         HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(fileName + "." + fileExt, request, response);
    }

    @GetMapping(value = "/img/{fileName}.{fileExt}")
    public ResponseEntity<String> img(@PathVariable("fileName") String fileName,
                                      @PathVariable("fileExt") String fileExt,
                                      HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(fileName + "." + fileExt, request, response);
    }

    @GetMapping(value = "/assets/images/{fileName}.{fileExt}")
    public ResponseEntity<String> assetsImages(@PathVariable("fileName") String fileName,
                                               @PathVariable("fileExt") String fileExt,
                                               HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(fileName + "." + fileExt, request, response);
    }

    @GetMapping(value = "/assets/{fileName}.{fileExt}")
    public ResponseEntity<String> assets(@PathVariable("fileName") String fileName,
                                         @PathVariable("fileExt") String fileExt,
                                         HttpServletRequest request, HttpServletResponse response) {
        return getAndSendImage(fileName + "." + fileExt, request, response);
    }
}