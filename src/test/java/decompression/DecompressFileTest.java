package decompression;

import compression.CompressFile;
import exception.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DecompressFileTest {

    @TempDir
    Path tempDir;

    private DecompressFile decompressor;
    private CompressFile compressor;
    private Path tempInputFile;
    private Path tempOutputFile;

    @BeforeEach
    void setUp() throws IOException {
        decompressor = new DecompressFile();
        compressor = new CompressFile();
        tempInputFile = tempDir.resolve("input.txt");
        tempOutputFile = tempDir.resolve("output.huff");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempInputFile);
        Files.deleteIfExists(tempOutputFile);
    }

    @Test
    void testDecompressNullInputPath() {
        assertThrows(InvalidArgumentException.class,
                () -> decompressor.decompress(null, tempOutputFile.toString()));
    }

    @Test
    void testDecompressEmptyInputPath() {
        assertThrows(InvalidArgumentException.class,
                () -> decompressor.decompress("", tempOutputFile.toString()));
    }

    @Test
    void testDecompressNullOutputPath() throws IOException {
        Files.write(tempInputFile, new byte[]{0, 0});
        assertThrows(InvalidArgumentException.class,
                () -> decompressor.decompress(tempInputFile.toString(), null));
    }

    @Test
    void testDecompressEmptyOutputPath() throws IOException {
        Files.write(tempInputFile, new byte[]{0, 0});
        assertThrows(InvalidArgumentException.class,
                () -> decompressor.decompress(tempInputFile.toString(), ""));
    }

    @Test
    void testDecompressNonExistentInputFile() {
        assertThrows(InvalidCompressedFileException.class,
                () -> decompressor.decompress("/nonexistent/file.huff", tempOutputFile.toString()));
    }

    @Test
    void testDecompressInputPathIsDirectory() {
        assertThrows(InvalidCompressedFileException.class,
                () -> decompressor.decompress(tempDir.toString(), tempOutputFile.toString()));
    }

    @Test
    void testDecompressFileTooSmall() throws IOException {
        Path smallFile = tempDir.resolve("small.huff");
        Files.write(smallFile, new byte[]{0});

        assertThrows(InvalidCompressedFileException.class,
                () -> decompressor.decompress(smallFile.toString(), tempOutputFile.toString()));
    }

    @Test
    void testDecompressSimpleTextGroupSize1() throws IOException {
        String originalText = "hello";
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressRepeatingCharacterGroupSize1() throws IOException {
        String originalText = "aaaaaaaaaa";
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressGroupSize2() throws IOException {
        String originalText = "AABBCCDDEE";
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 2);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressGroupSize3() throws IOException {
        String originalText = "ABCDEFGHIJKLMNO";
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 3);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressGroupSize4() throws IOException {
        String originalText = "ABCDABCDABCDXYZW";
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 4);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressBinaryData() throws IOException {
        byte[] originalData = new byte[100];
        for (int i = 0; i < 100; i++) {
            originalData[i] = (byte) (i % 256);
        }
        Files.write(tempInputFile, originalData);

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(originalData, decompressed);
    }

    @Test
    void testDecompressLargeFile() throws IOException {
        StringBuilder sb = new StringBuilder();
        String pattern = "The quick brown fox jumps over the lazy dog. ";
        for (int i = 0; i < 200; i++) {
            sb.append(pattern);
        }
        String originalText = sb.toString();
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressOddSizeFileGroupSize2() throws IOException {
        byte[] originalData = {0x01, 0x02, 0x03, 0x04, 0x05};
        Files.write(tempInputFile, originalData);

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 2);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(originalData, decompressed);
    }

    @Test
    void testDecompressOddSizeFileGroupSize3() throws IOException {
        byte[] originalData = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        Files.write(tempInputFile, originalData);

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 3);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(originalData, decompressed);
    }

    @Test
    void testDecompressOddSizeFileGroupSize4() throws IOException {
        byte[] originalData = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09};
        Files.write(tempInputFile, originalData);

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 4);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(originalData, decompressed);
    }

    @Test
    void testDecompressAllPrintableAscii() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (char c = 32; c < 127; c++) {
            sb.append(c);
        }
        String originalText = sb.toString();
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressAllGroupSizes() throws IOException {
        String originalText = "AABBCCDDEE";
        Files.write(tempInputFile, originalText.getBytes());

        for (int groupSize = 1; groupSize <= 4; groupSize++) {
            Path compressedFile = tempDir.resolve("compressed_" + groupSize + ".huff");
            Path decompressedFile = tempDir.resolve("decompressed_" + groupSize + ".txt");

            compressor.compress(tempInputFile.toString(), compressedFile.toString(), groupSize);
            decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

            byte[] decompressed = Files.readAllBytes(decompressedFile);
            assertEquals(originalText, new String(decompressed),
                    "Failed for group size " + groupSize);
        }
    }

    @Test
    void testIsValidCompressedFileValid() throws IOException {
        String originalText = "test";
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);

        assertTrue(decompressor.isValidCompressedFile(compressedFile.toString()));
    }

    @Test
    void testIsValidCompressedFileNonExistent() {
        assertFalse(decompressor.isValidCompressedFile("/nonexistent/file.huff"));
    }

    @Test
    void testIsValidCompressedFileTooSmall() throws IOException {
        Path smallFile = tempDir.resolve("small.huff");
        Files.write(smallFile, new byte[]{0});

        assertFalse(decompressor.isValidCompressedFile(smallFile.toString()));
    }

    @Test
    void testIsValidCompressedFileInvalidStructure() throws IOException {
        Path invalidFile = tempDir.resolve("invalid.huff");
        Files.write(invalidFile, new byte[]{0, 0, 0, 0, 0, 0});

        assertFalse(decompressor.isValidCompressedFile(invalidFile.toString()));
    }

    @Test
    void testIsValidCompressedFileAllGroupSizes() throws IOException {
        String originalText = "test data";
        Files.write(tempInputFile, originalText.getBytes());

        for (int groupSize = 1; groupSize <= 4; groupSize++) {
            Path compressedFile = tempDir.resolve("valid_" + groupSize + ".huff");
            compressor.compress(tempInputFile.toString(), compressedFile.toString(), groupSize);

            assertTrue(decompressor.isValidCompressedFile(compressedFile.toString()),
                    "Should be valid for group size " + groupSize);
        }
    }

    @Test
    void testDecompressSingleCharacter() throws IOException {
        Files.write(tempInputFile, new byte[]{0x42});

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(new byte[]{0x42}, decompressed);
    }

    @Test
    void testDecompressRepeatingPatternGroupSize2() throws IOException {
        String originalText = "ABABABABAB";
        Files.write(tempInputFile, originalText.getBytes());

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.txt");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 2);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertEquals(originalText, new String(decompressed));
    }

    @Test
    void testDecompressHighByteValues() throws IOException {
        byte[] originalData = {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC};
        Files.write(tempInputFile, originalData);

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(originalData, decompressed);
    }

    @Test
    void testDecompressAllZeroBytes() throws IOException {
        byte[] originalData = new byte[50];
        Files.write(tempInputFile, originalData);

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(originalData, decompressed);
    }

    @Test
    void testDecompressAllFFBytes() throws IOException {
        byte[] originalData = new byte[50];
        for (int i = 0; i < 50; i++) {
            originalData[i] = (byte) 0xFF;
        }
        Files.write(tempInputFile, originalData);

        Path compressedFile = tempDir.resolve("compressed.huff");
        Path decompressedFile = tempDir.resolve("decompressed.bin");

        compressor.compress(tempInputFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(originalData, decompressed);
    }
}