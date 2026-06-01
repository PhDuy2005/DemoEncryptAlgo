package com.encrpt.demo.DemoEncryptAlgo.controller;

import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.domain.dto.res.RestResponse;
import com.encrpt.demo.DemoEncryptAlgo.service.CryptoProcessService;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    private final CryptoProcessService cryptoProcessService;

    public CryptoController(CryptoProcessService cryptoProcessService) {
        this.cryptoProcessService = cryptoProcessService;
    }

    @PostMapping("/process")
    public ResponseEntity<RestResponse<String>> process(@RequestBody EncryptionRequest request) {
        try {
            AlgoName type = request.getEffectiveType();
            String result = cryptoProcessService.process(request);
            return success(request.getFeature() + " " + type + " thanh cong", result);
        } catch (IllegalArgumentException e) {
            return error(400, "Validation error", e.getMessage());
        } catch (GeneralSecurityException e) {
            return error(400, "Validation error", "Invalid key or ciphertext");
        } catch (Exception e) {
            return error(500, "Processing error", e.getMessage());
        }
    }

    private ResponseEntity<RestResponse<String>> success(String message, String data) {
        return ResponseEntity.ok(RestResponse.<String>builder()
                .statusCode(200)
                .error(null)
                .message(message)
                .data(data)
                .build());
    }

    private ResponseEntity<RestResponse<String>> error(int statusCode, String error, String message) {
        return ResponseEntity.status(statusCode).body(RestResponse.<String>builder()
                .statusCode(statusCode)
                .error(error)
                .message(message)
                .data(null)
                .build());
    }
}
