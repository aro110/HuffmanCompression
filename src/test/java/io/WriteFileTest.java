package io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WriteFileTest {

    @Test
    void testWriteSingleBits(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("bits.bin");
        try (WriteFile writer = new WriteFile(path.toString())) {
            writer.writeBit(1);
            writer.writeBit(0);
            writer.writeBit(1);
            writer.writeBit(0);
            writer.writeBit(1);
            writer.writeBit(0);
            writer.writeBit(1);
            writer.writeBit(0);
        }

        byte[] result = Files.readAllBytes(path);
        assertEquals(1, result.length);
        assertEquals((byte) 0xAA, result[0]);
    }

    @Test
    void testWriteBitsLong(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("long.bin");
        try (WriteFile writer = new WriteFile(path.toString())) {
            writer.writeBits(0xABCDL, 16);
        }

        byte[] result = Files.readAllBytes(path);
        assertEquals(2, result.length);
        assertEquals((byte) 0xAB, result[0]);
        assertEquals((byte) 0xCD, result[1]);
    }

    @Test
    void testPaddingOnClose(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("padding.bin");
        try (WriteFile writer = new WriteFile(path.toString())) {
            writer.writeBits(0b11, 2);
        }

        byte[] result = Files.readAllBytes(path);
        assertEquals(1, result.length);
        assertEquals((byte) 0xC0, result[0]);
    }

    @Test
    void testMultipleWriteBitsCalls(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("multiple.bin");
        try (WriteFile writer = new WriteFile(path.toString())) {
            writer.writeBits(0xF, 4);
            writer.writeBits(0b0101, 4);
        }

        byte[] result = Files.readAllBytes(path);
        assertEquals(1, result.length);
        assertEquals((byte) 0xF5, result[0]);
    }

    @Test
    void testWriteBitsAcrossByteBoundaries(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("boundary.bin");
        try (WriteFile writer = new WriteFile(path.toString())) {
            writer.writeBits(0b111, 3);
            writer.writeBits(0b111111, 6);
        }

        byte[] result = Files.readAllBytes(path);
        assertEquals(2, result.length);
        assertEquals((byte) 0xFF, result[0]);
        assertEquals((byte) 0x80, result[1]);
    }

    @Test
    void testWriteZeroBits(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("zero.bin");
        try (WriteFile writer = new WriteFile(path.toString())) {
            writer.writeBits(0xFFFF, 0);
        }

        byte[] result = Files.readAllBytes(path);
        assertEquals(0, result.length);
    }

    @Test
    void testWriteBitsLargerThanByte(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("large.bin");
        try (WriteFile writer = new WriteFile(path.toString())) {
            writer.writeBits(0b101010101, 9);
        }

        byte[] result = Files.readAllBytes(path);
        assertEquals(2, result.length);
        assertEquals((byte) 0xAA, result[0]);
        assertEquals((byte) 0x80, result[1]);
    }
}