package com.encrpt.demo.DemoEncryptAlgo.domain.dto.req;

import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionRequest {
    private AlgoName type;
    private FeatureOption feature;
    private String data;
    private String key;
    private InputType inputType;
    private InputType keyInputType;
    private InputType outputType;

    public AlgoName getEffectiveType() {
        return type;
    }
}
