package com.encrpt.demo.DemoEncryptAlgo.controller;

import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.domain.dto.res.RestResponse;
import com.encrpt.demo.DemoEncryptAlgo.service.CryptoService;
import com.encrpt.demo.DemoEncryptAlgo.service.RijndaelService;
import com.encrpt.demo.DemoEncryptAlgo.service.TwofishEncryptionService;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller chung cho tất cả các thuật toán mã hóa
 * Dispatch request tới service phù hợp dựa trên AlgoName
 */
@RestController
@RequestMapping("/api/encrypt")
public class EncryptController {

    @Autowired
    private TwofishEncryptionService twofishService;

    @Autowired
    private RijndaelService rijndaelService;

    @Autowired
    private CryptoService rc6Service;  // RC6

    @PostMapping("/process")
    public ResponseEntity<RestResponse<String>> process(@RequestBody EncryptionRequest request) {
        try {
            // Validate input
            if (request.getAlgoName() == null || request.getFeature() == null) {
                return buildErrorResponse(400, "Thiếu thông tin",
                    "Vui lòng cung cấp algoName và feature");
            }

            // Set default values
            InputType inputType = request.getInputType() != null ? request.getInputType() : InputType.BASE64;
            InputType keyInputType = request.getKeyInputType() != null ? request.getKeyInputType() : InputType.PLAIN_TEXT;
            InputType outputType = request.getOutputType() != null ? request.getOutputType() : InputType.BASE64;

            // DISPATCH dựa trên AlgoName
            String result;
            String message;

            switch (request.getAlgoName()) {
                case TWOFISH:
                    result = twofishService.processEncryption(
                        request.getData(), request.getKey(),
                        inputType, keyInputType, outputType,
                        request.getFeature()
                    );
                    message = "✅ " + request.getFeature() + " (TWOFISH) thành công";
                    break;

                case RINJADEL:
                    result = rijndaelService.processEncryption(
                        request.getData(), request.getKey(),
                        inputType, keyInputType, outputType,
                        request.getFeature()
                    );
                    message = "✅ " + request.getFeature() + " (RINJADEL) thành công";
                    break;

                case RC6:
                    result = rc6Service.processEncryption(
                        request.getData(), request.getKey(),
                        inputType, keyInputType, outputType,
                        request.getFeature()
                    );
                    message = "✅ " + request.getFeature() + " (RC6) thành công";
                    break;

                case SERPENT:
                    // TODO: Implement SERPENT
                    return buildErrorResponse(501, "Chưa implement",
                        "Thuật toán SERPENT chưa được implement");

                default:
                    return buildErrorResponse(400, "Thuật toán không hỗ trợ",
                        "Hỗ trợ: TWOFISH, RINJADEL, RC6");
            }

            return buildSuccessResponse(200, message, result);

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(400, "Lỗi validation", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(500, "Lỗi xử lý", e.getMessage());
        }
    }

    private ResponseEntity<RestResponse<String>> buildSuccessResponse(
            int code, String message, String data) {
        RestResponse<String> response = RestResponse.<String>builder()
                .statusCode(code)
                .error(null)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<RestResponse<String>> buildErrorResponse(
            int code, String error, String message) {
        RestResponse<String> response = RestResponse.<String>builder()
                .statusCode(code)
                .error(error)
                .message(message)
                .data(null)
                .build();
        
        if (code == 400) {
            return ResponseEntity.badRequest().body(response);
        } else if (code == 501) {
            return ResponseEntity.status(501).body(response);
        } else {
            return ResponseEntity.status(code).body(response);
        }
    }
}
