package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;

public interface EncryptionAlgorithmService {
    AlgoName type();

    String processEncryption(
            String data,
            String key,
            InputType inputType,
            InputType keyInputType,
            InputType outputType,
            FeatureOption feature) throws Exception;
}
