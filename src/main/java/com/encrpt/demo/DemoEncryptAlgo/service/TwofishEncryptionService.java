package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.Twofish_Algorithm;
import com.encrpt.demo.DemoEncryptAlgo.util.CryptoCodec;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import org.springframework.stereotype.Service;

@Service
public class TwofishEncryptionService extends AbstractEncryptionAlgorithmService {

    private static final int BLOCK_SIZE = 16;

    @Override
    public AlgoName type() {
        return AlgoName.TWOFISH;
    }

    @Override
    protected byte[] encrypt(byte[] data, byte[] key) throws Exception {
        CryptoCodec.requireKeyLength(key, 8, 16, 24, 32);
        Object sessionKey = Twofish_Algorithm.makeKey(key);
        byte[] paddedData = CryptoCodec.padPkcs7(data, BLOCK_SIZE);
        byte[] encrypted = new byte[paddedData.length];
        for (int i = 0; i < paddedData.length; i += BLOCK_SIZE) {
            byte[] block = Twofish_Algorithm.blockEncrypt(paddedData, i, sessionKey);
            System.arraycopy(block, 0, encrypted, i, BLOCK_SIZE);
        }
        return encrypted;
    }

    @Override
    protected byte[] decrypt(byte[] encryptedData, byte[] key) throws Exception {
        CryptoCodec.requireKeyLength(key, 8, 16, 24, 32);
        CryptoCodec.requireBlockMultiple(encryptedData, BLOCK_SIZE, "Ciphertext");
        Object sessionKey = Twofish_Algorithm.makeKey(key);
        byte[] decrypted = new byte[encryptedData.length];
        for (int i = 0; i < encryptedData.length; i += BLOCK_SIZE) {
            byte[] block = Twofish_Algorithm.blockDecrypt(encryptedData, i, sessionKey);
            System.arraycopy(block, 0, decrypted, i, BLOCK_SIZE);
        }
        return CryptoCodec.unpadPkcs7(decrypted, BLOCK_SIZE);
    }
}
