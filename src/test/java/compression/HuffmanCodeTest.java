package compression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanCodeTest {

    @Test
    void testSingleBitCode() {
        HuffmanCode code = new HuffmanCode("0");

        assertEquals(0, code.getBits());
        assertEquals(1, code.getLength());
    }

    @Test
    void testSingleBitCodeOne() {
        HuffmanCode code = new HuffmanCode("1");

        assertEquals(1, code.getBits());
        assertEquals(1, code.getLength());
    }

    @Test
    void testTwoBitCode() {
        HuffmanCode code = new HuffmanCode("10");

        assertEquals(2, code.getBits());
        assertEquals(2, code.getLength());
    }

    @Test
    void testThreeBitCode() {
        HuffmanCode code = new HuffmanCode("101");

        assertEquals(5, code.getBits());
        assertEquals(3, code.getLength());
    }

    @Test
    void testFourBitCode() {
        HuffmanCode code = new HuffmanCode("1010");

        assertEquals(10, code.getBits());
        assertEquals(4, code.getLength());
    }

    @Test
    void testAllZeros() {
        HuffmanCode code = new HuffmanCode("0000");

        assertEquals(0, code.getBits());
        assertEquals(4, code.getLength());
    }

    @Test
    void testAllOnes() {
        HuffmanCode code = new HuffmanCode("1111");

        assertEquals(15, code.getBits());
        assertEquals(4, code.getLength());
    }

    @Test
    void testLongCode() {
        HuffmanCode code = new HuffmanCode("10101010");

        assertEquals(170, code.getBits());
        assertEquals(8, code.getLength());
    }

    @Test
    void testEmptyCode() {
        HuffmanCode code = new HuffmanCode("");

        assertEquals(0, code.getBits());
        assertEquals(0, code.getLength());
    }

    @Test
    void testBinaryConversion00() {
        HuffmanCode code = new HuffmanCode("00");

        assertEquals(0, code.getBits());
        assertEquals(2, code.getLength());
    }

    @Test
    void testBinaryConversion01() {
        HuffmanCode code = new HuffmanCode("01");

        assertEquals(1, code.getBits());
        assertEquals(2, code.getLength());
    }

    @Test
    void testBinaryConversion10() {
        HuffmanCode code = new HuffmanCode("10");

        assertEquals(2, code.getBits());
        assertEquals(2, code.getLength());
    }

    @Test
    void testBinaryConversion11() {
        HuffmanCode code = new HuffmanCode("11");

        assertEquals(3, code.getBits());
        assertEquals(2, code.getLength());
    }

    @Test
    void testVeryLongCode() {
        String longCode = "1".repeat(20);
        HuffmanCode code = new HuffmanCode(longCode);

        assertEquals((1 << 20) - 1, code.getBits());
        assertEquals(20, code.getLength());
    }

    @Test
    void testAlternatingBits() {
        HuffmanCode code = new HuffmanCode("10101010");

        assertEquals(0b10101010, code.getBits());
        assertEquals(8, code.getLength());
    }

    @Test
    void testLeadingZeros() {
        HuffmanCode code = new HuffmanCode("001");

        assertEquals(1, code.getBits());
        assertEquals(3, code.getLength());
    }

    @Test
    void testTrailingZeros() {
        HuffmanCode code = new HuffmanCode("100");

        assertEquals(4, code.getBits());
        assertEquals(3, code.getLength());
    }

    @Test
    void testMaxValue8Bit() {
        HuffmanCode code = new HuffmanCode("11111111");

        assertEquals(255, code.getBits());
        assertEquals(8, code.getLength());
    }

    @Test
    void testPattern11001100() {
        HuffmanCode code = new HuffmanCode("11001100");

        assertEquals(0b11001100, code.getBits());
        assertEquals(8, code.getLength());
    }

    @Test
    void test16BitCode() {
        HuffmanCode code = new HuffmanCode("1111111111111111");

        assertEquals(65535, code.getBits());
        assertEquals(16, code.getLength());
    }

    @Test
    void test32BitCode() {
        String code32 = "1".repeat(32);
        HuffmanCode code = new HuffmanCode(code32);

        assertEquals(0xFFFFFFFFL, code.getBits());
        assertEquals(32, code.getLength());
    }
}