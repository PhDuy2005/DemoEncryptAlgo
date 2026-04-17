package com.encrpt.demo.DemoEncryptAlgo.controller;

import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.domain.dto.res.RestResponse;
import com.encrpt.demo.DemoEncryptAlgo.service.TwofishEncryptionService;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/twofish")
public class TwofishController {

    @Autowired
    private TwofishEncryptionService encryptionService;

    /**
     * Endpoint xử lý mã hóa/giải mã Twofish
     * 
     * @param request EncryptionRequest với algoName phải là TWOFISH
     * @return RestResponse với kết quả
     */
    @PostMapping("/process")
    public ResponseEntity<RestResponse<String>> process(@RequestBody EncryptionRequest request) {
        try {
            // ✅ Kiểm tra thuật toán phải là TWOFISH
            if (request.getAlgoName() != AlgoName.TWOFISH) {
                return buildErrorResponse(400, "Thuật toán không hỗ trợ",
                        "Endpoint này chỉ hỗ trợ thuật toán TWOFISH");
            }

            // ✅ Kiểm tra feature
            if (request.getFeature() == null) {
                return buildErrorResponse(400, "Thiếu thông tin",
                        "Vui lòng chỉ định feature (ENCRYPT hoặc DECRYPT)");
            }

            // ✅ Set mặc định cho InputType nếu null
            InputType inputType = request.getInputType() != null ? request.getInputType() : InputType.BASE64;
            InputType keyInputType = request.getKeyInputType() != null ? request.getKeyInputType()
                    : InputType.PLAIN_TEXT;
            InputType outputType = request.getOutputType() != null ? request.getOutputType() : InputType.BASE64;

            // ✅ Gọi service để xử lý toàn bộ logic
            String result = encryptionService.processEncryption(
                    request.getData(),
                    request.getKey(),
                    inputType,
                    keyInputType,
                    outputType,
                    request.getFeature());

            // ✅ Build success response
            String message = request.getFeature().toString().equals("ENCRYPT") ? "Mã hóa thành công"
                    : "Giải mã thành công";
            return buildSuccessResponse(200, message, result);

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(400, "Lỗi validation", "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(500, "Lỗi xử lý", "Lỗi: " + e.getMessage());
        }
    }

    /**
     * Build success response
     */
    private ResponseEntity<RestResponse<String>> buildSuccessResponse(int code, String message, String data) {
        RestResponse<String> response = RestResponse.<String>builder()
                .statusCode(code)
                .error(null)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Build error response
     */
    private ResponseEntity<RestResponse<String>> buildErrorResponse(int code, String error, String message) {
        RestResponse<String> response = RestResponse.<String>builder()
                .statusCode(code)
                .error(error)
                .message(message)
                .data(null)
                .build();

        return code == 400 ? ResponseEntity.badRequest().body(response) : ResponseEntity.status(code).body(response);
    }
}
