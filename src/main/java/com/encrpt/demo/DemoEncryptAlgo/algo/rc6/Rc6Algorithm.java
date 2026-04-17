package com.encrpt.demo.DemoEncryptAlgo.algo.rc6;
import java.util.Arrays;

import com.encrpt.demo.DemoEncryptAlgo.algo.CryptoAlgorithm;

public class Rc6Algorithm implements CryptoAlgorithm {

    private static final int r = 20;
    private final KeySchedule keySchedule = new KeySchedule();

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        byte[] padded = pad(data);
        int[] S = keySchedule.generateSubKeys(key);

        byte[] result = new byte[padded.length];

        for (int i = 0; i < padded.length; i += 16) {
            byte[] block = Arrays.copyOfRange(padded, i, i + 16);
            byte[] enc = encryptBlock(block, S);
            System.arraycopy(enc, 0, result, i, 16);
        }

        return result;
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) {
        int[] S = keySchedule.generateSubKeys(key);
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i += 16) {
            byte[] block = Arrays.copyOfRange(data, i, i + 16);
            byte[] dec = decryptBlock(block, S);
            System.arraycopy(dec, 0, result, i, 16);
        }

        return unpad(result);
    }

    // ================= BLOCK =================

    private byte[] encryptBlock(byte[] input, int[] S) {
        int A = bytesToInt(input, 0);
        int B = bytesToInt(input, 4);
        int C = bytesToInt(input, 8);
        int D = bytesToInt(input, 12);

        B += S[0];
        D += S[1];

        for (int i = 1; i <= r; i++) {
            int t = Integer.rotateLeft(B * (2 * B + 1), 5);
            int u = Integer.rotateLeft(D * (2 * D + 1), 5);

            A = Integer.rotateLeft(A ^ t, u) + S[2 * i];
            C = Integer.rotateLeft(C ^ u, t) + S[2 * i + 1];

            int temp = A;
            A = B;
            B = C;
            C = D;
            D = temp;
        }

        A += S[2 * r + 2];
        C += S[2 * r + 3];

        byte[] out = new byte[16];
        intToBytes(A, out, 0);
        intToBytes(B, out, 4);
        intToBytes(C, out, 8);
        intToBytes(D, out, 12);

        return out;
    }

    private byte[] decryptBlock(byte[] input, int[] S) {
        int A = bytesToInt(input, 0);
        int B = bytesToInt(input, 4);
        int C = bytesToInt(input, 8);
        int D = bytesToInt(input, 12);

        C -= S[2 * r + 3];
        A -= S[2 * r + 2];

        for (int i = r; i >= 1; i--) {
            int temp = D;
            D = C;
            C = B;
            B = A;
            A = temp;

            int u = Integer.rotateLeft(D * (2 * D + 1), 5);
            int t = Integer.rotateLeft(B * (2 * B + 1), 5);

            C = Integer.rotateRight(C - S[2 * i + 1], t) ^ u;
            A = Integer.rotateRight(A - S[2 * i], u) ^ t;
        }

        D -= S[1];
        B -= S[0];

        byte[] out = new byte[16];
        intToBytes(A, out, 0);
        intToBytes(B, out, 4);
        intToBytes(C, out, 8);
        intToBytes(D, out, 12);

        return out;
    }

    // ================= UTILS =================

    private int bytesToInt(byte[] b, int offset) {
        return (b[offset] & 0xff) |
                ((b[offset + 1] & 0xff) << 8) |
                ((b[offset + 2] & 0xff) << 16) |
                ((b[offset + 3] & 0xff) << 24);
    }

    private void intToBytes(int val, byte[] b, int offset) {
        b[offset] = (byte) val;
        b[offset + 1] = (byte) (val >> 8);
        b[offset + 2] = (byte) (val >> 16);
        b[offset + 3] = (byte) (val >> 24);
    }

    // ================= PADDING =================

    private byte[] pad(byte[] data) {
        int pad = 16 - (data.length % 16);
        byte[] result = Arrays.copyOf(data, data.length + pad);
        for (int i = data.length; i < result.length; i++) {
            result[i] = (byte) pad;
        }
        return result;
    }

    private byte[] unpad(byte[] data) {
        int pad = data[data.length - 1];
        return Arrays.copyOf(data, data.length - pad);
    }
}