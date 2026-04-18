package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.rc6.Rc6Algorithm;
import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CryptoService {

    private final Rc6Algorithm rc6Algorithm = new Rc6Algorithm();

    /**
     * Phương thức chính xử lý RC6 encrypt/decrypt với format conversion
     */
    public String processEncryption(
            String data, String key,
            InputType inputType, InputType keyInputType,
            InputType outputType, FeatureOption feature) throws Exception {

        // Decode input
        byte[] decodedData = parseInput(data, inputType);
        byte[] decodedKey = parseInput(key, keyInputType);

        // Xử lý (encrypt hoặc decrypt)
        byte[] result;
        if (feature == FeatureOption.ENCRYPT) {
            result = rc6Algorithm.encrypt(decodedData, decodedKey);
        } else if (feature == FeatureOption.DECRYPT) {
            result = rc6Algorithm.decrypt(decodedData, decodedKey);
        } else {
            throw new IllegalArgumentException("Feature không hợp lệ: " + feature);
        }

        // Encode output
        return formatOutput(result, outputType);
    }

    /**
     * Phương thức wrapper cho request DTO (sử dụng cho CryptoController)
     */
    public String process(EncryptionRequest req) {
        try {
            return processEncryption(
                    req.getData(), req.getKey(),
                    req.getInputType() != null ? req.getInputType() : InputType.BASE64,
                    req.getKeyInputType() != null ? req.getKeyInputType() : InputType.PLAIN_TEXT,
                    req.getOutputType() != null ? req.getOutputType() : InputType.BASE64,
                    req.getFeature());
        } catch (Exception e) {
            throw new RuntimeException("RC6 Processing failed: " + e.getMessage(), e);
        }
    }

    // ================= PARSE =================

    private byte[] parseInput(String input, InputType type) {
        if (input == null)
            return new byte[0];

        switch (type) {
            case BASE64:
                return Base64.getDecoder().decode(input);
            case PLAIN_TEXT:
                return input.getBytes(StandardCharsets.UTF_8);
            default:
                throw new RuntimeException("Unsupported input type");
        }
    }

    // ================= OUTPUT =================

    private String formatOutput(byte[] data, InputType type) {
        switch (type) {
            case BASE64:
                return Base64.getEncoder().encodeToString(data);
            case PLAIN_TEXT:
                return new String(data, StandardCharsets.UTF_8);
            default:
                throw new RuntimeException("Unsupported output type");
        }
    }
}