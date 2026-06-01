package com.encrpt.demo.DemoEncryptAlgo.util;

import com.encrpt.demo.DemoEncryptAlgo.util.enums.InputType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

public final class CryptoCodec {

    private CryptoCodec() {
    }

    public static byte[] decode(String value, InputType type, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException(fieldName + " input type must not be null");
        }

        try {
            return switch (type) {
                case PLAIN_TEXT -> value.getBytes(StandardCharsets.UTF_8);
                case BASE64 -> Base64.getDecoder().decode(value);
                case HEX -> HexFormat.of().parseHex(value);
            };
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(fieldName + " is not valid " + type + " data");
        }
    }

    public static String encode(byte[] value, InputType type) {
        if (type == null) {
            throw new IllegalArgumentException("outputType must not be null");
        }

        return switch (type) {
            case PLAIN_TEXT -> new String(value, StandardCharsets.UTF_8);
            case BASE64 -> Base64.getEncoder().encodeToString(value);
            case HEX -> HexFormat.of().formatHex(value);
        };
    }

    public static byte[] padPkcs7(byte[] data, int blockSize) {
        int paddingLength = blockSize - (data.length % blockSize);
        byte[] padded = Arrays.copyOf(data, data.length + paddingLength);
        Arrays.fill(padded, data.length, padded.length, (byte) paddingLength);
        return padded;
    }

    public static byte[] unpadPkcs7(byte[] data, int blockSize) {
        requireBlockMultiple(data, blockSize, "Padded data");
        int paddingLength = data[data.length - 1] & 0xFF;
        if (paddingLength < 1 || paddingLength > blockSize || paddingLength > data.length) {
            throw new IllegalArgumentException("Invalid PKCS#7 padding");
        }
        for (int i = data.length - paddingLength; i < data.length; i++) {
            if ((data[i] & 0xFF) != paddingLength) {
                throw new IllegalArgumentException("Invalid PKCS#7 padding");
            }
        }
        return Arrays.copyOf(data, data.length - paddingLength);
    }

    public static void requireKeyLength(byte[] key, int... validLengths) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be empty");
        }
        for (int validLength : validLengths) {
            if (key.length == validLength) {
                return;
            }
        }
        throw new IllegalArgumentException("Key length must be one of " + Arrays.toString(validLengths) + " bytes");
    }

    public static void requireKeyRange(byte[] key, int minLength, int maxLength) {
        if (key == null || key.length < minLength || key.length > maxLength) {
            throw new IllegalArgumentException(
                    "Key length must be between " + minLength + " and " + maxLength + " bytes");
        }
    }

    public static void requireBlockMultiple(byte[] data, int blockSize, String fieldName) {
        if (data == null || data.length == 0 || data.length % blockSize != 0) {
            throw new IllegalArgumentException(fieldName + " length must be a non-zero multiple of " + blockSize);
        }
    }
}
