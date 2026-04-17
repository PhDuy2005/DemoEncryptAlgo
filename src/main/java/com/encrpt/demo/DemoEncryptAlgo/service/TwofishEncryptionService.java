package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.Twofish_Algorithm;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.stereotype.Service;
import java.security.InvalidKeyException;
import java.util.Base64;

@Service
public class TwofishEncryptionService {

    private static final int BLOCK_SIZE = 16; // Twofish block size

    /**
     * Phương thức chính xử lý toàn bộ quy trình mã hóa/giải mã
     * 
     * @param data         Dữ liệu input (chuỗi)
     * @param key          Khóa input (chuỗi)
     * @param inputType    Định dạng input (BASE64 hoặc PLAIN_TEXT)
     * @param keyInputType Định dạng key (BASE64 hoặc PLAIN_TEXT)
     * @param outputType   Định dạng output (BASE64 hoặc PLAIN_TEXT)
     * @param feature      Hành động (ENCRYPT hoặc DECRYPT)
     * @return Chuỗi kết quả đã được encode theo outputType
     * @throws Exception Nếu xảy ra lỗi
     */
    public String processEncryption(
            String data,
            String key,
            InputType inputType,
            InputType keyInputType,
            InputType outputType,
            FeatureOption feature) throws Exception {

        // Decode input
        byte[] decodedData = decodeInput(data, inputType);
        byte[] decodedKey = decodeInput(key, keyInputType);

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
        return encodeOutput(result, outputType);
    }

    /**
     * Mã hóa dữ liệu sử dụng Twofish.
     * 
     * @param data Dữ liệu gốc (byte array).
     * @param key  Khóa bí mật (phải là 8, 16, 24, 32 bytes).
     * @return Dữ liệu đã mã hóa.
     */
    public byte[] encrypt(byte[] data, byte[] key) throws InvalidKeyException {
        Object sessionKey = Twofish_Algorithm.makeKey(key);
        // Twofish hoạt động trên block 16 byte, nên cần padding nếu data không đủ
        byte[] paddedData = padData(data);
        byte[] encrypted = new byte[paddedData.length];
        for (int i = 0; i < paddedData.length; i += BLOCK_SIZE) {
            byte[] block = Twofish_Algorithm.blockEncrypt(paddedData, i, sessionKey);
            System.arraycopy(block, 0, encrypted, i, BLOCK_SIZE);
        }
        return encrypted;
    }

    /**
     * Giải mã dữ liệu sử dụng Twofish.
     * 
     * @param encryptedData Dữ liệu đã mã hóa.
     * @param key           Khóa bí mật (giống như khi mã hóa).
     * @return Dữ liệu gốc.
     */
    public byte[] decrypt(byte[] encryptedData, byte[] key) throws InvalidKeyException {
        Object sessionKey = Twofish_Algorithm.makeKey(key);
        byte[] decrypted = new byte[encryptedData.length];
        for (int i = 0; i < encryptedData.length; i += BLOCK_SIZE) {
            byte[] block = Twofish_Algorithm.blockDecrypt(encryptedData, i, sessionKey);
            System.arraycopy(block, 0, decrypted, i, BLOCK_SIZE);
        }
        return unpadData(decrypted);
    }

    // Phương thức padding đơn giản (PKCS7-like) để đảm bảo data chia hết cho block
    // size
    private byte[] padData(byte[] data) {
        int paddingLength = BLOCK_SIZE - (data.length % BLOCK_SIZE);
        byte[] padded = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) paddingLength;
        }
        return padded;
    }

    // Unpad data
    private byte[] unpadData(byte[] data) {
        int paddingLength = data[data.length - 1] & 0xFF;
        byte[] unpadded = new byte[data.length - paddingLength];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }

    /**
     * Helper: Decode input dựa trên inputType
     */
    private byte[] decodeInput(String input, InputType inputType) throws Exception {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input không được trống");
        }

        if (inputType == InputType.BASE64) {
            return Base64.getDecoder().decode(input);
        } else if (inputType == InputType.PLAIN_TEXT) {
            return input.getBytes();
        } else {
            throw new IllegalArgumentException("InputType không hợp lệ: " + inputType);
        }
    }

    /**
     * Helper: Encode output dựa trên outputType
     */
    private String encodeOutput(byte[] output, InputType outputType) {
        if (outputType == InputType.BASE64) {
            return Base64.getEncoder().encodeToString(output);
        } else if (outputType == InputType.PLAIN_TEXT) {
            return new String(output);
        } else {
            throw new IllegalArgumentException("OutputType không hợp lệ: " + outputType);
        }
    }
}