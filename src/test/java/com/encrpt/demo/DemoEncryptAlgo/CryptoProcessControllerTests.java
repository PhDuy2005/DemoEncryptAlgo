package com.encrpt.demo.DemoEncryptAlgo;

import com.encrpt.demo.DemoEncryptAlgo.controller.CryptoController;
import com.encrpt.demo.DemoEncryptAlgo.service.BlowfishService;
import com.encrpt.demo.DemoEncryptAlgo.service.CryptoProcessService;
import com.encrpt.demo.DemoEncryptAlgo.service.MarsService;
import com.encrpt.demo.DemoEncryptAlgo.service.Rc6Service;
import com.encrpt.demo.DemoEncryptAlgo.service.RijndaelService;
import com.encrpt.demo.DemoEncryptAlgo.service.SerpentService;
import com.encrpt.demo.DemoEncryptAlgo.service.TwofishEncryptionService;
import com.encrpt.demo.DemoEncryptAlgo.util.enums.AlgoName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CryptoProcessControllerTests {

    private static final String ENDPOINT = "/api/crypto/process";
    private static final String KEY_16_BYTES = "demo-key-1234567";
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CryptoProcessService service = new CryptoProcessService(List.of(
                new RijndaelService(),
                new TwofishEncryptionService(),
                new Rc6Service(),
                new SerpentService(),
                new MarsService(),
                new BlowfishService()));
        mockMvc = MockMvcBuilders.standaloneSetup(new CryptoController(service)).build();
    }

    @Test
    void roundTripPlainTextForAllAlgorithms() throws Exception {
        String plaintext = "Demo encryption API - tieng Viet co dau: Xin chao lop ATTT!";

        for (AlgoName algorithm : AlgoName.values()) {
            String encrypted = extractString(postJson("""
                    {
                      "type": "%s",
                      "feature": "ENCRYPT",
                      "data": "%s",
                      "key": "%s"
                    }
                    """.formatted(algorithm, plaintext, KEY_16_BYTES), 200), "data");

            String decrypted = postJson("""
                    {
                      "type": "%s",
                      "feature": "DECRYPT",
                      "data": "%s",
                      "key": "%s",
                      "inputType": "BASE64",
                      "outputType": "PLAIN_TEXT"
                    }
                    """.formatted(algorithm, encrypted, KEY_16_BYTES), 200);

            assertThat(extractString(decrypted, "data")).as(algorithm.name()).isEqualTo(plaintext);
            assertThat(hasNullField(decrypted, "error")).isTrue();
        }
    }

    @Test
    void invalidKeyLengthReturnsBadRequestForAllAlgorithms() throws Exception {
        for (AlgoName algorithm : AlgoName.values()) {
            String response = postJson("""
                    {
                      "type": "%s",
                      "feature": "ENCRYPT",
                      "data": "hello",
                      "key": "bad"
                    }
                    """.formatted(algorithm), 400);

            assertThat(extractInt(response, "statusCode")).as(algorithm.name()).isEqualTo(400);
            assertThat(hasNullField(response, "data")).isTrue();
        }
    }

    @Test
    void wrongDecryptKeyReturnsBadRequest() throws Exception {
        String encrypted = extractString(postJson("""
                {
                  "type": "RIJNDAEL",
                  "feature": "ENCRYPT",
                  "data": "secret text",
                  "key": "%s"
                }
                """.formatted(KEY_16_BYTES), 200), "data");

        String response = postJson("""
                {
                  "type": "RIJNDAEL",
                  "feature": "DECRYPT",
                  "data": "%s",
                  "key": "wrong-key-123456",
                  "inputType": "BASE64",
                  "outputType": "PLAIN_TEXT"
                }
                """.formatted(encrypted), 400);

        assertThat(extractInt(response, "statusCode")).isEqualTo(400);
    }

    @Test
    void invalidBase64AndHexReturnBadRequest() throws Exception {
        String base64Response = postJson("""
                {
                  "type": "RC6",
                  "feature": "DECRYPT",
                  "data": "not-base64%%",
                  "key": "%s",
                  "inputType": "BASE64"
                }
                """.formatted(KEY_16_BYTES), 400);

        String hexResponse = postJson("""
                {
                  "type": "SERPENT",
                  "feature": "ENCRYPT",
                  "data": "123Z",
                  "key": "%s",
                  "inputType": "HEX"
                }
                """.formatted(KEY_16_BYTES), 400);

        assertThat(extractInt(base64Response, "statusCode")).isEqualTo(400);
        assertThat(extractInt(hexResponse, "statusCode")).isEqualTo(400);
    }

    @Test
    void ciphertextWithInvalidBlockSizeReturnsBadRequest() throws Exception {
        String fiveBytesBase64 = Base64.getEncoder().encodeToString("abcde".getBytes());

        String response = postJson("""
                {
                  "type": "TWOFISH",
                  "feature": "DECRYPT",
                  "data": "%s",
                  "key": "%s",
                  "inputType": "BASE64",
                  "outputType": "PLAIN_TEXT"
                }
                """.formatted(fiveBytesBase64, KEY_16_BYTES), 400);

        assertThat(extractInt(response, "statusCode")).isEqualTo(400);
    }

    private String postJson(String json, int expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(expectedStatus))
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private String extractString(String json, String fieldName) {
        Matcher matcher = Pattern.compile("\"" + fieldName + "\":\"([^\"]*)\"").matcher(json);
        assertThat(matcher.find()).as(json).isTrue();
        return matcher.group(1);
    }

    private int extractInt(String json, String fieldName) {
        Matcher matcher = Pattern.compile("\"" + fieldName + "\":(\\d+)").matcher(json);
        assertThat(matcher.find()).as(json).isTrue();
        return Integer.parseInt(matcher.group(1));
    }

    private boolean hasNullField(String json, String fieldName) {
        return json.contains("\"" + fieldName + "\":null");
    }
}
