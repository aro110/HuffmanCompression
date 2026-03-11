package util;

import io.ReadFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

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
    void countFrequenciesPathGroupSize1CountsBytes() throws IOException {
        Files.write(tempFile, new byte[]{0x41, 0x42, 0x41, 0x43});

        dataStructure.HashMap<Long, Long> frequencies = Utils.countFrequencies(tempFile.toString(), 1);

        assertEquals(2L, frequencies.get(0x41L));
        assertEquals(1L, frequencies.get(0x42L));
        assertEquals(1L, frequencies.get(0x43L));
        assertEquals(3, frequencies.size());
    }

    @Test
    void countFrequenciesPathGroupSize2CountsFullGroups() throws IOException {
        Files.write(tempFile, new byte[]{0x41, 0x42, 0x41, 0x42, 0x43, 0x44});

        dataStructure.HashMap<Long, Long> frequencies = Utils.countFrequencies(tempFile.toString(), 2);

        assertEquals(2L, frequencies.get(0x4142L));
        assertEquals(1L, frequencies.get(0x4344L));
        assertEquals(2, frequencies.size());
    }

    @Test
    void countFrequenciesPathPadsTrailingBytesForNonDivisibleGroupSize() throws IOException {
        Files.write(tempFile, new byte[]{0x41, 0x42, 0x43, 0x44, 0x45});

        dataStructure.HashMap<Long, Long> frequencies = Utils.countFrequencies(tempFile.toString(), 3);

        assertEquals(1L, frequencies.get(0x414243L));
        assertEquals(1L, frequencies.get(0x444500L));
        assertEquals(2, frequencies.size());
    }

    @Test
    void countFrequenciesPathEmptyFileReturnsEmptyMap() throws IOException {
        Files.write(tempFile, new byte[]{});

        dataStructure.HashMap<Long, Long> frequencies = Utils.countFrequencies(tempFile.toString(), 1);

        assertEquals(0, frequencies.size());
    }

    @Test
    void countFrequenciesPathThrowsForMissingFile() {
        Path missing = tempFile.resolveSibling(tempFile.getFileName().toString() + ".missing");

        assertThrows(IOException.class, () -> Utils.countFrequencies(missing.toString(), 1));
    }

    @Test
    void countFrequenciesPathGroupSizeZeroThrows() throws IOException {
        Files.write(tempFile, new byte[]{0x41});

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Utils.countFrequencies(tempFile.toString(), 0));
    }

    @Test
    void countFrequenciesPathNegativeGroupSizeThrows() throws IOException {
        Files.write(tempFile, new byte[]{0x41});

        assertThrows(NegativeArraySizeException.class, () -> Utils.countFrequencies(tempFile.toString(), -1));
    }

    @Test
    void bytesToSymbolReturnsZeroWhenBytesReadIsZero() {
        byte[] bytes = {(byte) 0x12, (byte) 0x34};
        assertEquals(0L, Utils.bytesToSymbol(bytes, 0));
    }

    @Test
    void bytesToSymbolPaddedShiftsLeftToGroupSize() {
        byte[] bytes = {(byte) 0x12, (byte) 0x34};
        assertEquals(0x12340000L, Utils.bytesToSymbolPadded(bytes, 2, 4));
    }

    @Test
    void symbolToBytesRoundTripForMaxUnsigned32BitValue() {
        long symbol = 0xFFFF_FFFFL;
        byte[] bytes = Utils.symbolToBytes(symbol, 4);

        assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, bytes);
        assertEquals(symbol, Utils.bytesToSymbol(bytes, 4));
    }

}