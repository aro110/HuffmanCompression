package compression;

import decompression.DecompressFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CompressFileTest {

    private CompressFile compressor;
    private DecompressFile decompressor;
    private Path tempInputFile;
    private Path tempOutputFile;
    private Path tempDecodedFile;

    @BeforeEach
    void setUp() throws IOException {
        compressor = new CompressFile();
        decompressor = new DecompressFile();
        tempInputFile = Files.createTempFile("input", ".txt");
        tempOutputFile = Files.createTempFile("output", ".huff");
        tempDecodedFile = Files.createTempFile("decoded", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempInputFile);
        Files.deleteIfExists(tempOutputFile);
        Files.deleteIfExists(tempDecodedFile);
    }

    @Test
    void testCompressAndDecompressSingleByteGroupSize1() throws IOException {
        Files.write(tempInputFile, new byte[]{0x42});

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressSimpleTextGroupSize1() throws IOException {
        String text = "AAABBBCCC";
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressGroupSize2() throws IOException {
        String text = "AABBCCDDEE";
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 2);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressGroupSize3() throws IOException {
        String text = "ABCABCABCDEFDEF";
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 3);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressGroupSize4() throws IOException {
        String text = "ABCDABCDABCDXYZWXYZW";
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 4);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressOddSizeGroupSize2() throws IOException {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 2);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressOddSizeGroupSize3() throws IOException {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 3);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressOddSizeGroupSize4() throws IOException {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 4);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressLargeFile() throws IOException {
        byte[] data = new byte[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAndDecompressRepeatingPattern() throws IOException {
        String text = "AAAAAABBBBBBCCCCCCDDDDDD";
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressedFileCreated() throws IOException {
        String text = "Test content";
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);

        assertTrue(Files.exists(tempOutputFile));
        assertTrue(Files.size(tempOutputFile) > 0);
    }

    @Test
    void testCompressAllGroupSizes() throws IOException {
        String text = "AABBCCDDEE";
        Files.write(tempInputFile, text.getBytes());

        for (int groupSize = 1; groupSize <= 4; groupSize++) {
            Path outputFile = Files.createTempFile("output_" + groupSize, ".huff");
            Path decodedFile = Files.createTempFile("decoded_" + groupSize, ".txt");

            try {
                compressor.compress(tempInputFile.toString(), outputFile.toString(), groupSize);
                decompressor.decompress(outputFile.toString(), decodedFile.toString());

                byte[] original = Files.readAllBytes(tempInputFile);
                byte[] decoded = Files.readAllBytes(decodedFile);

                assertArrayEquals(original, decoded, "Failed for group size " + groupSize);
            } finally {
                Files.deleteIfExists(outputFile);
                Files.deleteIfExists(decodedFile);
            }
        }
    }

    @Test
    void testCompressBinaryData() throws IOException {
        byte[] data = new byte[100];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (Math.random() * 256);
        }
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressHighByteValues() throws IOException {
        byte[] data = {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressSingleRepeatedByte() throws IOException {
        byte[] data = new byte[100];
        for (int i = 0; i < 100; i++) {
            data[i] = 0x42;
        }
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAllPrintableAscii() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (char c = 32; c < 127; c++) {
            sb.append(c);
        }
        String text = sb.toString();
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressRepeatingPatternGroupSize2() throws IOException {
        String text = "ABABABABAB";
        Files.write(tempInputFile, text.getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 2);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressLongText() throws IOException {
        StringBuilder sb = new StringBuilder();
        String pattern = "The quick brown fox jumps over the lazy dog. ";
        for (int i = 0; i < 100; i++) {
            sb.append(pattern);
        }
        Files.write(tempInputFile, sb.toString().getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAllZeroBytes() throws IOException {
        byte[] data = new byte[50];
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressAllFFBytes() throws IOException {
        byte[] data = new byte[50];
        for (int i = 0; i < 50; i++) {
            data[i] = (byte) 0xFF;
        }
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressTwoBytes() throws IOException {
        byte[] data = {0x41, 0x42};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressGroupSize2ExactMultiple() throws IOException {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 2);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressGroupSize3ExactMultiple() throws IOException {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 3);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressGroupSize4ExactMultiple() throws IOException {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        Files.write(tempInputFile, data);

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 4);
        decompressor.decompress(tempOutputFile.toString(), tempDecodedFile.toString());

        byte[] original = Files.readAllBytes(tempInputFile);
        byte[] decoded = Files.readAllBytes(tempDecodedFile);

        assertArrayEquals(original, decoded);
    }

    @Test
    void testCompressionReducesSize() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("AAAA");
        }
        Files.write(tempInputFile, sb.toString().getBytes());

        compressor.compress(tempInputFile.toString(), tempOutputFile.toString(), 1);

        long originalSize = Files.size(tempInputFile);
        long compressedSize = Files.size(tempOutputFile);

        assertTrue(compressedSize < originalSize);
    }
}