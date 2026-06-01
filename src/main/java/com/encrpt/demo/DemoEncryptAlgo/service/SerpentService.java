package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.SerpentAlgorithm;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import org.springframework.stereotype.Service;

@Service
public class SerpentService extends AbstractEncryptionAlgorithmService {

    private final SerpentAlgorithm algorithm = new SerpentAlgorithm();

    @Override
    public AlgoName type() {
        return AlgoName.SERPENT;
    }

    @Override
    protected byte[] encrypt(byte[] data, byte[] key) {
        return algorithm.encrypt(data, key);
    }

    @Override
    protected byte[] decrypt(byte[] data, byte[] key) {
        return algorithm.decrypt(data, key);
    }
}
