package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.MarsAlgorithm;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import org.springframework.stereotype.Service;

@Service
public class MarsService extends AbstractEncryptionAlgorithmService {

    private final MarsAlgorithm algorithm = new MarsAlgorithm();

    @Override
    public AlgoName type() {
        return AlgoName.MARS;
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
