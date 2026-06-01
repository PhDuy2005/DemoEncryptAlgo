package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CryptoProcessService {

    private final Map<AlgoName, EncryptionAlgorithmService> services = new EnumMap<>(AlgoName.class);

    public CryptoProcessService(List<EncryptionAlgorithmService> algorithmServices) {
        for (EncryptionAlgorithmService algorithmService : algorithmServices) {
            services.put(algorithmService.type(), algorithmService);
        }
    }

    public String process(EncryptionRequest request) throws Exception {
        AlgoName type = request.getEffectiveType();
        FeatureOption feature = request.getFeature();
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (feature == null) {
            throw new IllegalArgumentException("feature is required");
        }

        EncryptionAlgorithmService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported algorithm: " + type);
        }

        InputType inputType = request.getInputType();
        if (inputType == null) {
            inputType = feature == FeatureOption.ENCRYPT ? InputType.PLAIN_TEXT : InputType.BASE64;
        }

        InputType keyInputType = request.getKeyInputType() != null
                ? request.getKeyInputType()
                : InputType.PLAIN_TEXT;
        InputType outputType = request.getOutputType() != null
                ? request.getOutputType()
                : InputType.BASE64;

        return service.processEncryption(
                request.getData(),
                request.getKey(),
                inputType,
                keyInputType,
                outputType,
                feature);
    }
}
