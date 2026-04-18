package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class RijndaelService {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_IV = "1234567890123456";

    /**
     * Phương thức chính xử lý Rijndael encrypt/decrypt với format conversion
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
            result = encrypt(decodedData, decodedKey);
        } else if (feature == FeatureOption.DECRYPT) {
            result = decrypt(decodedData, decodedKey);
        } else {
            throw new IllegalArgumentException("Feature không hợp lệ: " + feature);
        }

        // Encode output
        return formatOutput(result, outputType);
    }

    // ================= HELPERS =================

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

    // ================= ENCRYPT/DECRYPT =================

    public byte[] encrypt(byte[] plainTextBytes, byte[] keyBytes) throws Exception {
        validateKey(keyBytes);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(DEFAULT_IV.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(plainTextBytes);
    }

    public byte[] decrypt(byte[] cipherTextBytes, byte[] keyBytes) throws Exception {
        validateKey(keyBytes);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(DEFAULT_IV.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(cipherTextBytes);
    }

    private void validateKey(byte[] keyBytes) {
        if (keyBytes == null || (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32)) {
            throw new IllegalArgumentException("Secret key bắt buộc phải có độ dài 16, 24, hoặc 32 byte!");
        }
    }
}