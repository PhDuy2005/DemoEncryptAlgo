package com.encrpt.demo.DemoEncryptAlgo.algo;

public interface CryptoAlgorithm {
    byte[] encrypt(byte[] data, byte[] key);
    byte[] decrypt(byte[] data, byte[] key);
}