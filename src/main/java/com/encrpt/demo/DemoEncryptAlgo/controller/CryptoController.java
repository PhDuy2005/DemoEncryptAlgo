package com.encrpt.demo.DemoEncryptAlgo.controller;

import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @PostMapping
    public String process(@RequestBody EncryptionRequest request) {
        return cryptoService.process(request);
    }
}