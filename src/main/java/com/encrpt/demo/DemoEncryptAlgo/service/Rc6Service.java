package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.rc6.Rc6Algorithm;
import com.encrpt.demo.DemoEncryptAlgo.util.CryptoCodec;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import org.springframework.stereotype.Service;

@Service
public class Rc6Service extends AbstractEncryptionAlgorithmService {

    private static final int BLOCK_SIZE = 16;
    private final Rc6Algorithm rc6Algorithm = new Rc6Algorithm();

    @Override
    public AlgoName type() {
        return AlgoName.RC6;
    }

    @Override
    protected byte[] encrypt(byte[] data, byte[] key) {
        CryptoCodec.requireKeyLength(key, 16, 24, 32);
        return rc6Algorithm.encrypt(data, key);
    }

    @Override
    protected byte[] decrypt(byte[] data, byte[] key) {
        CryptoCodec.requireKeyLength(key, 16, 24, 32);
        CryptoCodec.requireBlockMultiple(data, BLOCK_SIZE, "Ciphertext");
        return rc6Algorithm.decrypt(data, key);
    }
}
