package com.encrpt.demo.DemoEncryptAlgo.controller;

import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.domain.dto.res.RestResponse;
import com.encrpt.demo.DemoEncryptAlgo.service.TwofishEncryptionService;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;

@RestController
@RequestMapping("/api/twofish")
public class TwofishController {

    @Autowired
    private TwofishEncryptionService encryptionService;

    /**
     * Endpoint chung cho Twofish
     * Sử dụng FeatureOption để quyết định mã hóa hay giải mã
     * Hỗ trợ cả input Base64 lẫn plain text
     */
    @PostMapping("/process")
    public ResponseEntity<RestResponse<String>> process(@RequestBody EncryptionRequest request) {
        try {
            // Xác nhận thuật toán phải là TWOFISH
            if (request.getAlgoName() != AlgoName.TWOFISH) {
                RestResponse<String> response = RestResponse.<String>builder()
                        .statusCode(400)
                        .error("Thuật toán không hỗ trợ")
                        .message("Chỉ hỗ trợ thuật toán TWOFISH tại endpoint này")
                        .build();
                return ResponseEntity.badRequest().body(response);
            }

            // Xác nhận feature không được null
            if (request.getFeature() == null) {
                RestResponse<String> response = RestResponse.<String>builder()
                        .statusCode(400)
                        .error("Thiếu thông tin")
                        .message("Vui lòng chỉ định feature (ENCRYPT hoặc DECRYPT)")
                        .build();
                return ResponseEntity.badRequest().body(response);
            }

            // Lấy inputType, mặc định là BASE64
            InputType inputType = request.getInputType() != null ? request.getInputType() : InputType.BASE64;

            // Lấy keyInputType, mặc định là PLAIN_TEXT (khóa thường là plain text)
            InputType keyInputType = request.getKeyInputType() != null ? request.getKeyInputType() : InputType.PLAIN_TEXT;

            // Decode data và key dựa trên các inputType riêng biệt
            byte[] data = decodeInput(request.getData(), inputType);
            byte[] key = decodeInput(request.getKey(), keyInputType);
            
            byte[] result;
            String message;

            // Quyết định dựa trên FeatureOption
            if (request.getFeature() == FeatureOption.ENCRYPT) {
                result = encryptionService.encrypt(data, key);
                message = "Mã hóa thành công";
            } else if (request.getFeature() == FeatureOption.DECRYPT) {
                result = encryptionService.decrypt(data, key);
                message = "Giải mã thành công";
            } else {
                RestResponse<String> response = RestResponse.<String>builder()
                        .statusCode(400)
                        .error("Feature không hợp lệ")
                        .message("Feature phải là ENCRYPT hoặc DECRYPT")
                        .build();
                return ResponseEntity.badRequest().body(response);
            }

            // Lấy outputType, mặc định là BASE64
            InputType outputType = request.getOutputType() != null ? request.getOutputType() : InputType.BASE64;
            
            // Encode output dựa trên outputType
            String encodedResult = encodeOutput(result, outputType);
            
            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(200)
                    .message(message)
                    .data(encodedResult)
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(400)
                    .error("Định dạng dữ liệu không hợp lệ")
                    .message("Lỗi: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(500)
                    .error("Lỗi xử lý")
                    .message("Lỗi: " + e.getMessage())
                    .build();
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Helper method để decode input dựa trên inputType
     * @param input Chuỗi input (Base64 hoặc plain text)
     * @param inputType Loại input (BASE64 hoặc PLAIN_TEXT)
     * @return byte[] từ input
     */
    private byte[] decodeInput(String input, InputType inputType) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input không được trống");
        }

        if (inputType == InputType.BASE64) {
            return Base64.getDecoder().decode(input);
        } else if (inputType == InputType.PLAIN_TEXT) {
            return input.getBytes();
        } else {
            throw new IllegalArgumentException("InputType không hợp lệ");
        }
    }

    /**
     * Helper method để encode output dựa trên outputType
     * @param output byte[] từ algorithm
     * @param outputType Loại output (BASE64 hoặc PLAIN_TEXT)
     * @return String đã được encode
     */
    private String encodeOutput(byte[] output, InputType outputType) {
        if (outputType == InputType.BASE64) {
            return Base64.getEncoder().encodeToString(output);
        } else if (outputType == InputType.PLAIN_TEXT) {
            return new String(output);
        } else {
            throw new IllegalArgumentException("OutputType không hợp lệ");
        }
    }
}
