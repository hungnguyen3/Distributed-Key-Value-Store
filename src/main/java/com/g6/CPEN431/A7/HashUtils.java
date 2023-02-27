package com.g6.CPEN431.A7;
import com.google.common.hash.Hashing;

public class HashUtils {

    /**
     * Computes the 32-bit Murmur3 hash of the given byte array.
     * @param data the input data to hash
     * @return the 32-bit hash value as an integer
     */
    public static int hash(byte[] data) {
        return Math.abs(Hashing.murmur3_32_fixed(5).hashBytes(data).asInt());
    }
}
