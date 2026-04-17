package com.encrpt.demo.DemoEncryptAlgo.controller;

import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.RijndaelRequest;
import com.encrpt.demo.DemoEncryptAlgo.domain.dto.res.RestResponse;
import com.encrpt.demo.DemoEncryptAlgo.service.RijndaelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;

@RestController
@RequestMapping("/api/rijndael")
public class RijndaelController {

    @Autowired
    private RijndaelService encryptionService;

    @PostMapping("/encrypt")
    public ResponseEntity<RestResponse<String>> encrypt(@RequestBody RijndaelRequest request) {
        try {
            byte[] dataBytes = request.getData().getBytes();
            byte[] keyBytes = request.getKey().getBytes();

            byte[] encryptedBytes = encryptionService.encrypt(dataBytes, keyBytes);
            
            String encodedResult = Base64.getEncoder().encodeToString(encryptedBytes);

            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(200)
                    .message("Mã hóa Rijndael thành công")
                    .data(encodedResult)
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(400)
                    .error("Định dạng dữ liệu không hợp lệ")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(500)
                    .error("Lỗi hệ thống")
                    .message("Đã xảy ra lỗi trong quá trình mã hóa")
                    .build();
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<RestResponse<String>> decrypt(@RequestBody RijndaelRequest request) {
        try {
            byte[] dataBytes = Base64.getDecoder().decode(request.getData());
            byte[] keyBytes = request.getKey().getBytes();

            byte[] decryptedBytes = encryptionService.decrypt(dataBytes, keyBytes);
            
            String decodedResult = new String(decryptedBytes);

            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(200)
                    .message("Giải mã Rijndael thành công")
                    .data(decodedResult)
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(400)
                    .error("Dữ liệu đầu vào không hợp lệ")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            RestResponse<String> response = RestResponse.<String>builder()
                    .statusCode(500)
                    .error("Lỗi giải mã")
                    .message("Đã xảy ra lỗi (có thể sai Secret Key hoặc chuỗi Base64 không hợp lệ)")
                    .build();
            return ResponseEntity.status(500).body(response);
        }
    }
}