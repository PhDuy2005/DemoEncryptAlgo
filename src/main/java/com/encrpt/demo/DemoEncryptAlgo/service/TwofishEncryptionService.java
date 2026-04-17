package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.Twofish_Algorithm;
import org.springframework.stereotype.Service;
import java.security.InvalidKeyException;

@Service
public class TwofishEncryptionService {

    private static final int BLOCK_SIZE = 16; // Twofish block size

    /**
     * Mã hóa dữ liệu sử dụng Twofish.
     * 
     * @param data Dữ liệu gốc (byte array).
     * @param key  Khóa bí mật (phải là 128, 192, hoặc 256 bit).
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
}