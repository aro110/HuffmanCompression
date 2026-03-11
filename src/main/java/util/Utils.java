package util;

import dataStructure.HashMap;
import exception.FileReadException;
import io.ReadFile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    private static final int BUFFER_SIZE = 8192;

    public static HashMap<Long, Long> countFrequencies(String inputPath, int groupSize) throws IOException {
        HashMap<Long, Long> frequencies = new HashMap<>(4096);

        try (InputStream is = new BufferedInputStream(new FileInputStream(inputPath), BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            byte[] symbolBuffer = new byte[groupSize];
            int symbolPos = 0;
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    symbolBuffer[symbolPos++] = buffer[i];

                    if (symbolPos == groupSize) {
                        long symbol = bytesToSymbol(symbolBuffer, groupSize);
                        frequencies.put(symbol, frequencies.getOrDefault(symbol, 0L) + 1L);
                        symbolPos = 0;
                    }
                }
            }

            if (symbolPos > 0) {
                long symbol = bytesToSymbolPadded(symbolBuffer, symbolPos, groupSize);
                frequencies.put(symbol, frequencies.getOrDefault(symbol, 0L) + 1L);
            }
        }

        return frequencies;
    }

    public static long bytesToSymbol(byte[] bytes, int bytesRead) {
        long symbol = 0;
        for (int i = 0; i < bytesRead; i++) {
            symbol = (symbol << 8) | (bytes[i] & 0xFF);
        }
        return symbol;
    }

    public static long bytesToSymbolPadded(byte[] bytes, int bytesRead, int groupSize) {
        return bytesToSymbol(bytes, bytesRead) << ((groupSize - bytesRead) * 8);
    }

    public static byte[] symbolToBytes(long symbol, int groupSize) {
        byte[] bytes = new byte[groupSize];
        for (int i = groupSize - 1; i >= 0; i--) {
            bytes[i] = (byte) (symbol & 0xFF);
            symbol >>= 8;
        }
        return bytes;
    }
}