package com.encrpt.demo.DemoEncryptAlgo.algo.rc6;

public class KeySchedule {

    private static final int Pw = 0xB7E15163;
    private static final int Qw = 0x9E3779B9;
    private static final int r = 20;

    public int[] generateSubKeys(byte[] key) {
        int c = (key.length + 3) / 4;
        int[] L = new int[c];

        // convert key → L[] (little-endian)
        for (int i = key.length - 1; i >= 0; i--) {
            L[i / 4] = (L[i / 4] << 8) + (key[i] & 0xFF);
        }

        int t = 2 * r + 4;
        int[] S = new int[t];

        S[0] = Pw;
        for (int i = 1; i < t; i++) {
            S[i] = S[i - 1] + Qw;
        }

        int A = 0, B = 0, i = 0, j = 0;
        int v = 3 * Math.max(c, t);

        for (int k = 0; k < v; k++) {
            A = S[i] = Integer.rotateLeft(S[i] + A + B, 3);
            B = L[j] = Integer.rotateLeft(L[j] + A + B, (A + B));
            i = (i + 1) % t;
            j = (j + 1) % c;
        }

        return S;
    }
}