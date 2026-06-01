package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.util.CryptoCodec;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;

public abstract class AbstractEncryptionAlgorithmService implements EncryptionAlgorithmService {

    @Override
    public final String processEncryption(
            String data,
            String key,
            InputType inputType,
            InputType keyInputType,
            InputType outputType,
            FeatureOption feature) throws Exception {

        byte[] decodedData = CryptoCodec.decode(data, inputType, "data");
        byte[] decodedKey = CryptoCodec.decode(key, keyInputType, "key");
        byte[] result = switch (feature) {
            case ENCRYPT -> encrypt(decodedData, decodedKey);
            case DECRYPT -> decrypt(decodedData, decodedKey);
        };
        return CryptoCodec.encode(result, outputType);
    }

    protected abstract byte[] encrypt(byte[] data, byte[] key) throws Exception;

    protected abstract byte[] decrypt(byte[] data, byte[] key) throws Exception;
}
