package util;

import exception.InvalidArgumentException;

public class ArgumentParser {
    private String inputPath;
    private String outputPath;
    private boolean decompress = false;
    private boolean compress = false;
    private int groupSize = 1;
    private boolean groupSizeSet = false;

    public ArgumentParser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i", "--input" -> {
                    if (inputPath != null) {
                        throw new InvalidArgumentException("Duplicate input path argument (-i)");
                    }
                    inputPath = args[++i];
                }
                case "-o", "--output" -> {
                    if (outputPath != null) {
                        throw new InvalidArgumentException("Duplicate output path argument (-o)");
                    }
                    outputPath = args[++i];
                }
                case "-d", "--decompress" -> {
                    if (decompress) {
                        throw new InvalidArgumentException("Duplicate decompress argument (-d)");
                    }
                    decompress = true;
                }
                case "-c", "--compress" -> {
                    if (compress) {
                        throw new InvalidArgumentException("Duplicate compress argument (-c)");
                    }
                    compress = true;
                }
                case "-l" -> {
                    if (groupSizeSet) {
                        throw new InvalidArgumentException("Duplicate group size argument (-l)");
                    }
                    groupSize = parseGroupSize(args[++i]);
                    groupSizeSet = true;
                }
                case "-h", "--help" -> printHelpAndExit();
                default -> throw new InvalidArgumentException("Unknown argument: " + args[i]);
            }
        }
        validate();
    }

    private void validate() {
        if (inputPath == null) {
            throw new InvalidArgumentException("Input path is required (-i)");
        }
        if (outputPath == null) {
            outputPath = inputPath + (decompress ? ".decoded" : ".huff");
        }
        if (!compress && !decompress) {
            throw new InvalidArgumentException("Either compress (-c) or decompress (-d) must be specified");
        }
    }

    private int parseGroupSize(String value) {
        try {
            int size = Integer.parseInt(value);
            if (size < 1 || size > 4) {
                throw new InvalidArgumentException("Group size (-l) must be between 1 and 4, got: " + size);
            }
            return size;
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Invalid group size (-l): " + value + ". Must be a number between 1 and 4");
        }
    }

    private void printHelpAndExit() {
        System.out.println("""
            Huffman Compression Tool
            Usage: java -jar huffman.jar [options]

            Options:
              -i, --input <file>    Input file (required)
              -o, --output <file>   Output file (optional)
              -d, --decompress      Decompress mode
              -c , --compress       Compress mode
              -l <number>           Group size for compression (1-4 bytes, default: 1)
              -h, --help            Show this help
            """);
        System.exit(0);
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public boolean isCompress() {
        return compress;
    }

    public boolean isDecompress() {
        return decompress;
    }

    public int getGroupSize() {
        return groupSize;
    }
}