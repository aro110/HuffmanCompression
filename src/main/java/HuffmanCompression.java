import compression.CompressFile;
import decompression.DecompressFile;
import util.ArgumentParser;

public class HuffmanCompression {
    public static void main(String[] args) {
        ArgumentParser parser = new ArgumentParser(args);
        if (parser.isCompress()) {
            CompressFile compressFile = new CompressFile();
            compressFile.compress(parser.getInputPath(), parser.getOutputPath(), parser.getGroupSize());
            System.out.println("Compressed file: " + parser.getOutputPath());
        } else if (parser.isDecompress()) {
            DecompressFile decompressFile = new DecompressFile();
            decompressFile.decompress(parser.getInputPath(), parser.getOutputPath());
        }
    }
}