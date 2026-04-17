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
    private String data; // Dữ liệu cần xử lý (Base64 hoặc plain text)
    private String key; // Khóa bí mật (Base64 hoặc plain text)
    private AlgoName algoName; // Thuật toán (TWOFISH, RINJADEL, SERPENT, RC6)
    private FeatureOption feature; // Tính năng (ENCRYPT hoặc DECRYPT)
    private InputType inputType; // Định dạng input data (BASE64 hoặc PLAIN_TEXT) - mặc định Base64
    private InputType keyInputType; // Định dạng input key (BASE64 hoặc PLAIN_TEXT) - mặc định PLAIN_TEXT
    private InputType outputType; // Định dạng output (BASE64 hoặc PLAIN_TEXT) - mặc định Base64
}
