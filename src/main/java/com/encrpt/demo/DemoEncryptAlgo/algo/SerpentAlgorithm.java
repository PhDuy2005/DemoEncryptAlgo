package com.encrpt.demo.DemoEncryptAlgo.algo;

import com.encrpt.demo.DemoEncryptAlgo.util.CryptoCodec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SerpentAlgorithm implements CryptoAlgorithm {

    private static final int BLOCK_SIZE = 16;
    private static final int ROUNDS = 32;
    private static final long CONST = 0x9E3779B97F4A7C15L;

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        CryptoCodec.requireKeyLength(key, 16, 24, 32);
        long[] roundKeys = expandKey(key);
        byte[] padded = CryptoCodec.padPkcs7(data, BLOCK_SIZE);
        byte[] result = new byte[padded.length];
        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            encryptBlock(padded, i, result, i, roundKeys);
        }
        return result;
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) {
        CryptoCodec.requireKeyLength(key, 16, 24, 32);
        CryptoCodec.requireBlockMultiple(data, BLOCK_SIZE, "Ciphertext");
        long[] roundKeys = expandKey(key);
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i += BLOCK_SIZE) {
            decryptBlock(data, i, result, i, roundKeys);
        }
        return CryptoCodec.unpadPkcs7(result, BLOCK_SIZE);
    }

    private void encryptBlock(byte[] input, int inputOffset, byte[] output, int outputOffset, long[] roundKeys) {
        long left = toLong(input, inputOffset);
        long right = toLong(input, inputOffset + 8);
        for (int round = 0; round < ROUNDS; round++) {
            long nextLeft = right;
            long nextRight = left ^ roundFunction(right, roundKeys[round], round);
            left = nextLeft;
            right = nextRight;
        }
        writeLong(left, output, outputOffset);
        writeLong(right, output, outputOffset + 8);
    }

    private void decryptBlock(byte[] input, int inputOffset, byte[] output, int outputOffset, long[] roundKeys) {
        long left = toLong(input, inputOffset);
        long right = toLong(input, inputOffset + 8);
        for (int round = ROUNDS - 1; round >= 0; round--) {
            long previousRight = left;
            long previousLeft = right ^ roundFunction(previousRight, roundKeys[round], round);
            left = previousLeft;
            right = previousRight;
        }
        writeLong(left, output, outputOffset);
        writeLong(right, output, outputOffset + 8);
    }

    private long[] expandKey(byte[] key) {
        long[] seeds = new long[] { CONST, ~CONST, 0x243F6A8885A308D3L, 0x13198A2E03707344L };
        for (int i = 0; i < key.length; i++) {
            int bucket = i % seeds.length;
            seeds[bucket] = Long.rotateLeft(seeds[bucket] ^ (key[i] & 0xFFL) ^ (i * CONST), (i % 31) + 1);
        }

        long[] roundKeys = new long[ROUNDS];
        for (int i = 0; i < ROUNDS; i++) {
            long value = seeds[i % seeds.length] + CONST * (i + 1);
            value ^= Long.rotateLeft(seeds[(i + 1) % seeds.length], (i % 23) + 3);
            roundKeys[i] = Long.rotateLeft(value, i % 64);
        }
        return roundKeys;
    }

    private long roundFunction(long value, long roundKey, int round) {
        long mixed = value ^ roundKey;
        mixed = Long.rotateLeft(mixed, 13) + Long.rotateLeft(mixed, 37);
        mixed ^= Long.rotateLeft(roundKey, (round % 29) + 1);
        return mixed ^ (mixed >>> 17) ^ (mixed << 31);
    }

    private long toLong(byte[] input, int offset) {
        return ByteBuffer.wrap(input, offset, Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    private void writeLong(long value, byte[] output, int offset) {
        ByteBuffer.wrap(output, offset, Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(value);
    }
}
