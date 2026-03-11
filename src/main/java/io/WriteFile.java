package io;

import java.io.*;

public class WriteFile implements AutoCloseable {
    private final BufferedOutputStream out;
    private int currentByte;
    private int numBits;

    public WriteFile(String path) throws IOException {
        this.out = new BufferedOutputStream(new FileOutputStream(path), 65536);
        this.currentByte = 0;
        this.numBits = 0;
    }

    public void writeBit(int bit) throws IOException {
        currentByte = (currentByte << 1) | (bit & 1);
        numBits++;
        if (numBits == 8) {
            out.write(currentByte);
            currentByte = 0;
            numBits = 0;
        }
    }

    public void writeBits(long value, int len) throws IOException {
        if (len <= 0) return;

        while (len > 0) {
            int available = 8 - numBits;
            int bitsToWrite = Math.min(len, available);
            int shift = len - bitsToWrite;
            int mask = (1 << bitsToWrite) - 1;
            int bits = (int) ((value >> shift) & mask);

            currentByte = (currentByte << bitsToWrite) | bits;
            numBits += bitsToWrite;
            len -= bitsToWrite;

            if (numBits == 8) {
                out.write(currentByte);
                currentByte = 0;
                numBits = 0;
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (numBits > 0) {
            currentByte <<= (8 - numBits);
            out.write(currentByte);
        }
        out.close();
    }
}