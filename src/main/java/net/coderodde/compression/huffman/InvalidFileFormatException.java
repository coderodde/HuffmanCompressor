package net.coderodde.compression.huffman;

public class InvalidFileFormatException extends RuntimeException {

    public InvalidFileFormatException(String errorMessage) {
        super(errorMessage);
    }
}
