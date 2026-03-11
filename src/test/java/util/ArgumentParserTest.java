package util;

import exception.InvalidArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTest {

    @Test
    void testCompressMode() {
        String[] args = {"-c", "-i", "input.txt"};
        ArgumentParser parser = new ArgumentParser(args);

        assertTrue(parser.isCompress());
        assertFalse(parser.isDecompress());
        assertEquals("input.txt", parser.getInputPath());
        assertEquals("input.txt.huff", parser.getOutputPath());
        assertEquals(1, parser.getGroupSize());
    }

    @Test
    void testDecompressMode() {
        String[] args = {"-d", "-i", "input.huff"};
        ArgumentParser parser = new ArgumentParser(args);

        assertFalse(parser.isCompress());
        assertTrue(parser.isDecompress());
        assertEquals("input.huff", parser.getInputPath());
        assertEquals("input.huff.decoded", parser.getOutputPath());
    }

    @Test
    void testCustomOutputPath() {
        String[] args = {"-c", "-i", "input.txt", "-o", "output.huff"};
        ArgumentParser parser = new ArgumentParser(args);

        assertEquals("input.txt", parser.getInputPath());
        assertEquals("output.huff", parser.getOutputPath());
    }

    @Test
    void testGroupSizeL1() {
        String[] args = {"-c", "-i", "input.txt", "-l", "1"};
        ArgumentParser parser = new ArgumentParser(args);

        assertEquals(1, parser.getGroupSize());
    }

    @Test
    void testGroupSizeL2() {
        String[] args = {"-c", "-i", "input.txt", "-l", "2"};
        ArgumentParser parser = new ArgumentParser(args);

        assertEquals(2, parser.getGroupSize());
    }

    @Test
    void testGroupSizeL3() {
        String[] args = {"-c", "-i", "input.txt", "-l", "3"};
        ArgumentParser parser = new ArgumentParser(args);

        assertEquals(3, parser.getGroupSize());
    }

    @Test
    void testGroupSizeL4() {
        String[] args = {"-c", "-i", "input.txt", "-l", "4"};
        ArgumentParser parser = new ArgumentParser(args);

        assertEquals(4, parser.getGroupSize());
    }

    @Test
    void testInvalidGroupSizeTooSmall() {
        String[] args = {"-c", "-i", "input.txt", "-l", "0"};

        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class,
                () -> new ArgumentParser(args)
        );

        assertTrue(exception.getMessage().contains("must be between 1 and 4"));
    }

    @Test
    void testInvalidGroupSizeTooLarge() {
        String[] args = {"-c", "-i", "input.txt", "-l", "5"};

        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class,
                () -> new ArgumentParser(args)
        );

        assertTrue(exception.getMessage().contains("must be between 1 and 4"));
    }

    @Test
    void testInvalidGroupSizeNotNumber() {
        String[] args = {"-c", "-i", "input.txt", "-l", "abc"};

        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class,
                () -> new ArgumentParser(args)
        );

        assertTrue(exception.getMessage().contains("Must be a number"));
    }

    @Test
    void testMissingInputPath() {
        String[] args = {"-c"};

        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class,
                () -> new ArgumentParser(args)
        );

        assertTrue(exception.getMessage().contains("Input path is required"));
    }

    @Test
    void testMissingMode() {
        String[] args = {"-i", "input.txt"};

        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class,
                () -> new ArgumentParser(args)
        );

        assertTrue(exception.getMessage().contains("compress (-c) or decompress (-d) must be specified"));
    }

    @Test
    void testUnknownArgument() {
        String[] args = {"-c", "-i", "input.txt", "-x"};

        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class,
                () -> new ArgumentParser(args)
        );

        assertTrue(exception.getMessage().contains("Unknown argument"));
    }

    @Test
    void testLongFormArguments() {
        String[] args = {"--compress", "--input", "input.txt", "--output", "output.huff"};
        ArgumentParser parser = new ArgumentParser(args);

        assertTrue(parser.isCompress());
        assertEquals("input.txt", parser.getInputPath());
        assertEquals("output.huff", parser.getOutputPath());
    }

    @Test
    void testDecompressLongForm() {
        String[] args = {"--decompress", "--input", "input.huff"};
        ArgumentParser parser = new ArgumentParser(args);

        assertTrue(parser.isDecompress());
        assertEquals("input.huff", parser.getInputPath());
    }
}
