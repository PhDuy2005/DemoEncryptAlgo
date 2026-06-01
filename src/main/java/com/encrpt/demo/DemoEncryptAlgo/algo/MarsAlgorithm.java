package com.encrpt.demo.DemoEncryptAlgo.algo;

import com.encrpt.demo.DemoEncryptAlgo.util.CryptoCodec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MarsAlgorithm implements CryptoAlgorithm {

    private static final int BLOCK_SIZE = 16;
    private static final int ROUNDS = 32;
    private static final long CONST = 0xD1310BA698DFB5ACL;

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
        long[] seeds = new long[] { CONST, ~CONST, 0xA4093822299F31D0L, 0x082EFA98EC4E6C89L };
        for (int i = 0; i < key.length; i++) {
            int bucket = i % seeds.length;
            long value = (key[i] & 0xFFL) + CONST + i;
            seeds[bucket] = Long.rotateLeft(seeds[bucket] + value, (i % 19) + 5) ^ (value << (i % 8));
        }

        long[] roundKeys = new long[ROUNDS];
        for (int i = 0; i < ROUNDS; i++) {
            long value = seeds[i % seeds.length] ^ Long.rotateLeft(seeds[(i + 2) % seeds.length], (i % 31) + 1);
            value += CONST * (i + 3);
            roundKeys[i] = value ^ Long.rotateRight(value, (i % 17) + 7);
        }
        return roundKeys;
    }

    private long roundFunction(long value, long roundKey, int round) {
        long mixed = value + roundKey + (round * CONST);
        mixed ^= Long.rotateLeft(mixed, 7) ^ Long.rotateRight(roundKey, (round % 23) + 1);
        mixed += (mixed << 9) ^ (mixed >>> 11);
        return Long.rotateLeft(mixed, (round % 41) + 3);
    }

    private long toLong(byte[] input, int offset) {
        return ByteBuffer.wrap(input, offset, Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    private void writeLong(long value, byte[] output, int offset) {
        ByteBuffer.wrap(output, offset, Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(value);
    }
}
