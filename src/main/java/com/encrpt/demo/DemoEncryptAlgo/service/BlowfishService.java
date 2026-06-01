package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.util.CryptoCodec;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class BlowfishService extends AbstractEncryptionAlgorithmService {

    private static final int BLOCK_SIZE = 8;
    private static final String ALGORITHM = "Blowfish";
    private static final String CIPHER_TRANSFORMATION = "Blowfish/CBC/PKCS5Padding";
    private static final byte[] DEFAULT_IV = "12345678".getBytes(StandardCharsets.UTF_8);

    @Override
    public AlgoName type() {
        return AlgoName.BLOWFISH;
    }

    @Override
    protected byte[] encrypt(byte[] plainTextBytes, byte[] keyBytes) throws Exception {
        CryptoCodec.requireKeyRange(keyBytes, 4, 56);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(DEFAULT_IV));
        return cipher.doFinal(plainTextBytes);
    }

    @Override
    protected byte[] decrypt(byte[] cipherTextBytes, byte[] keyBytes) throws Exception {
        CryptoCodec.requireKeyRange(keyBytes, 4, 56);
        CryptoCodec.requireBlockMultiple(cipherTextBytes, BLOCK_SIZE, "Ciphertext");
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(DEFAULT_IV));
        return cipher.doFinal(cipherTextBytes);
    }
}
