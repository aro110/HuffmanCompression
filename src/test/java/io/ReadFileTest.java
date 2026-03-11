package io;

import exception.InvalidArgumentException;
import exception.PathNotExistException;
import exception.UnexpectedEndOfFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReadFileTest {

    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".bin");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }

    @Test
    void testReadBit() throws IOException {
        Files.write(tempFile, new byte[]{(byte) 0b10101010});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(1, reader.readBit());
            assertEquals(0, reader.readBit());
            assertEquals(1, reader.readBit());
            assertEquals(0, reader.readBit());
            assertEquals(1, reader.readBit());
            assertEquals(0, reader.readBit());
            assertEquals(1, reader.readBit());
            assertEquals(0, reader.readBit());
        }
    }

    @Test
    void testReadByte() throws IOException {
        Files.write(tempFile, new byte[]{0x42});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals((byte) 0x42, reader.readByte());
        }
    }

    @Test
    void testReadBits() throws IOException {
        Files.write(tempFile, new byte[]{(byte) 0xFF});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(0b1111, reader.readBits(4));
            assertEquals(0b1111, reader.readBits(4));
        }
    }

    @Test
    void testReadBitsLargeNumber() throws IOException {
        Files.write(tempFile, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(-1, reader.readBits(32));
        }
    }

    @Test
    void testReadBitsZero() throws IOException {
        Files.write(tempFile, new byte[]{(byte) 0x42});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(0, reader.readBits(0));
        }
    }

    @Test
    void testReadBitsInvalidNegative() throws IOException {
        Files.write(tempFile, new byte[]{0x42});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertThrows(InvalidArgumentException.class, () -> reader.readBits(-1));
        }
    }

    @Test
    void testReadBitsInvalidTooLarge() throws IOException {
        Files.write(tempFile, new byte[]{0x42});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertThrows(InvalidArgumentException.class, () -> reader.readBits(33));
        }
    }

    @Test
    void testHasMoreBits() throws IOException {
        Files.write(tempFile, new byte[]{0x42});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertTrue(reader.hasMoreBits());
            reader.readByte();
            assertFalse(reader.hasMoreBits());
        }
    }

    @Test
    void testGetTotalBitsRead() throws IOException {
        Files.write(tempFile, new byte[]{0x42, 0x43});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(0, reader.getTotalBitsRead());
            reader.readBit();
            assertEquals(1, reader.getTotalBitsRead());
            reader.readBits(7);
            assertEquals(8, reader.getTotalBitsRead());
            reader.readByte();
            assertEquals(16, reader.getTotalBitsRead());
        }
    }

    @Test
    void testGetFileSize() throws IOException {
        Files.write(tempFile, new byte[]{0x01, 0x02, 0x03, 0x04});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(4, reader.getFileSize());
        }
    }

    @Test
    void testGetTotalBitsInFile() throws IOException {
        Files.write(tempFile, new byte[]{0x01, 0x02, 0x03, 0x04});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(32, reader.getTotalBitsInFile());
        }
    }

    @Test
    void testGetRemainingBits() throws IOException {
        Files.write(tempFile, new byte[]{0x42, 0x43});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(16, reader.getRemainingBits());
            reader.readBit();
            assertEquals(15, reader.getRemainingBits());
            reader.readByte();
            assertEquals(7, reader.getRemainingBits());
        }
    }

    @Test
    void testReadBitBeyondEOF() throws IOException {
        Files.write(tempFile, new byte[]{0x42});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            reader.readByte();
            assertThrows(UnexpectedEndOfFileException.class, reader::readBit);
        }
    }

    @Test
    void testOpenStream() throws IOException {
        byte[] data = {0x01, 0x02, 0x03};
        Files.write(tempFile, data);

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            try (InputStream stream = reader.openStream()) {
                assertEquals(0x01, stream.read());
                assertEquals(0x02, stream.read());
                assertEquals(0x03, stream.read());
                assertEquals(-1, stream.read());
            }
        }
    }

    @Test
    void testFileNotFound() {
        assertThrows(PathNotExistException.class, () -> new ReadFile("nonexistent.txt"));
    }

    @Test
    void testEmptyFile() throws IOException {
        Files.write(tempFile, new byte[]{});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(0, reader.getFileSize());
            assertEquals(0, reader.getTotalBitsInFile());
            assertFalse(reader.hasMoreBits());
        }
    }

    @Test
    void testReadMultipleBytes() throws IOException {
        Files.write(tempFile, new byte[]{0x01, 0x02, 0x03, 0x04});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals((byte) 0x01, reader.readByte());
            assertEquals((byte) 0x02, reader.readByte());
            assertEquals((byte) 0x03, reader.readByte());
            assertEquals((byte) 0x04, reader.readByte());
        }
    }

    @Test
    void testMixedBitAndByteReading() throws IOException {
        Files.write(tempFile, new byte[]{(byte) 0b11110000, (byte) 0b10101010});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(0b1111, reader.readBits(4));
            assertEquals(0b0000, reader.readBits(4));
            assertEquals((byte) 0b10101010, reader.readByte());
        }
    }

    @Test
    void testRead3Bits() throws IOException {
        Files.write(tempFile, new byte[]{(byte) 0b11100000});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(0b111, reader.readBits(3));
        }
    }

    @Test
    void testRead32Bits() throws IOException {
        Files.write(tempFile, new byte[]{0x12, 0x34, 0x56, 0x78});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            assertEquals(0x12345678, reader.readBits(32));
        }
    }

    @Test
    void testOpenStreamMultipleTimes() throws IOException {
        Files.write(tempFile, new byte[]{0x01, 0x02});

        try (ReadFile reader = new ReadFile(tempFile.toString())) {
            try (InputStream stream1 = reader.openStream()) {
                assertEquals(0x01, stream1.read());
            }
            try (InputStream stream2 = reader.openStream()) {
                assertEquals(0x01, stream2.read());
            }
        }
    }
}