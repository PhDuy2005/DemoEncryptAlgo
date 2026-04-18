package com.encrpt.demo.DemoEncryptAlgo.service;

import com.encrpt.demo.DemoEncryptAlgo.algo.CryptoAlgorithm;
import com.encrpt.demo.DemoEncryptAlgo.algo.rc6.Rc6Algorithm;
import com.encrpt.demo.DemoEncryptAlgo.domain.dto.req.EncryptionRequest;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.FeatureOption;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class CryptoService {

    private final Map<AlgoName, CryptoAlgorithm> algorithms = new HashMap<>();

    public CryptoService() {
        // đăng ký thuật toán
        algorithms.put(AlgoName.RC6, new Rc6Algorithm());

        // TODO: thêm các thuật toán khác
        // algorithms.put(AlgoName.TWOFISH, new TwofishAlgorithm());
        // algorithms.put(AlgoName.RINJADEL, new RijndaelAlgorithm());
        // algorithms.put(AlgoName.SERPENT, new SerpentAlgorithm());
    }

    public String process(EncryptionRequest req) {

        // ===== 1. Parse input =====
        byte[] data = parseInput(req.getData(), req.getInputType());
        byte[] key  = parseInput(req.getKey(), req.getKeyInputType());

        // ===== 2. Lấy thuật toán =====
        CryptoAlgorithm algorithm = algorithms.get(req.getAlgoName());

        if (algorithm == null) {
            throw new RuntimeException("Unsupported algorithm: " + req.getAlgoName());
        }

        // ===== 3. Encrypt / Decrypt =====
        byte[] result;

        if (req.getFeature() == FeatureOption.ENCRYPT) {
            result = algorithm.encrypt(data, key);
        } else {
            result = algorithm.decrypt(data, key);
        }

        // ===== 4. Format output =====
        return formatOutput(result, req.getOutputType());
    }

    // ================= PARSE =================

    private byte[] parseInput(String input, InputType type) {
        if (input == null) return new byte[0];

        switch (type) {
            case BASE64:
                return Base64.getDecoder().decode(input);
            case PLAIN_TEXT:
                return input.getBytes(StandardCharsets.UTF_8);
            default:
                throw new RuntimeException("Unsupported input type");
        }
    }

    // ================= OUTPUT =================

    private String formatOutput(byte[] data, InputType type) {
        switch (type) {
            case BASE64:
                return Base64.getEncoder().encodeToString(data);
            case PLAIN_TEXT:
                return new String(data, StandardCharsets.UTF_8);
            default:
                throw new RuntimeException("Unsupported output type");
        }
    }
}