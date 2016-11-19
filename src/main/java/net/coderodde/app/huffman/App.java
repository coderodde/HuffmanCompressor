package net.coderodde.app.huffman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.coderodde.compression.huffman.BitString;
import net.coderodde.compression.huffman.CharacterWeightComputer;
import net.coderodde.compression.huffman.HuffmanDecoder;
import net.coderodde.compression.huffman.HuffmanDeserializer;
import net.coderodde.compression.huffman.HuffmanEncoder;
import net.coderodde.compression.huffman.HuffmanSerializer;
import net.coderodde.compression.huffman.HuffmanTree;

public final class App {

    private static final String ENCODE_OPTION_SHORT = "-e";
    private static final String ENCODE_OPTION_LONG  = "--encode";
    private static final String DECODE_OPTION_SHORT = "-d";
    private static final String DECODE_OPTION_LONG  = "--decode";
    private static final String HELP_OPTION_SHORT = "-h";
    private static final String HELP_OPTION_LONG  = "--help";
    private static final String VERSION_OPTION_SHORT = "-v";
    private static final String VERSION_OPTION_LONG  = "--version";
    private static final String ENCODED_FILE_EXTENSION = "het";
    
    public static void main(String[] args) {
        Set<String> commandLineArgumentSet = getCommandLineOptions(args);
        
        if (commandLineArgumentSet.isEmpty() 
                || commandLineArgumentSet.contains(HELP_OPTION_LONG)
                || commandLineArgumentSet.contains(HELP_OPTION_SHORT)) {
            printHelpMessage();
            System.exit(0);
        }
        
        if (commandLineArgumentSet.contains(VERSION_OPTION_LONG)
                || commandLineArgumentSet.contains(VERSION_OPTION_SHORT)) {
            printVersion();
            System.exit(0);
        }
        
        boolean decode = commandLineArgumentSet.contains(DECODE_OPTION_LONG) ||
                         commandLineArgumentSet.contains(DECODE_OPTION_SHORT);
        
        boolean encode = commandLineArgumentSet.contains(ENCODE_OPTION_LONG) ||
                         commandLineArgumentSet.contains(ENCODE_OPTION_SHORT);
        
        if (!decode && !encode) {
            printHelpMessage();
            System.exit(0);
        }
        
        if (decode && encode) {
            printHelpMessage();
            System.exit(0);
        }
        
        commandLineArgumentSet.removeAll(Arrays.asList(ENCODE_OPTION_SHORT,
                                                       ENCODE_OPTION_LONG,
                                                       DECODE_OPTION_SHORT,
                                                       DECODE_OPTION_LONG));
        if (commandLineArgumentSet.isEmpty()) {
            System.err.println("Bad command line format.");
            System.exit(1);
        }
        
        File file = new File(commandLineArgumentSet.iterator().next());
        
        try {
            if (decode) {
                doDecode(args);
            } else if (encode) {
                doEncode(file);
            } 
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    private static void doEncode(File file) throws FileNotFoundException {
        byte[] fileBytes = readBytes(file);
        
        Map<Byte, Float> weightMap =
                new CharacterWeightComputer()
                        .computeCharacterWeights(fileBytes);
        
        Map<Byte, BitString> encodeMap = 
                new HuffmanTree(weightMap).inferEncodingMap();
        
        BitString encodedText = new HuffmanEncoder().encode(encodeMap,
                                                            fileBytes);
        
        byte[] data = new HuffmanSerializer().serialize(encodeMap,
                                                        encodedText);
        
        File outputFile = 
                new File(file.getName() + "." + ENCODED_FILE_EXTENSION);
        
        System.out.println(
            "Writing compressed text to \"" + outputFile.getName() + "\"...");
        
        writeBytes(data, outputFile);
    }
    
    private static void doDecode(String[] args) {
        String file1 = null;
        String file2 = null;

        try {
            int index = 0;
            
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("DECODE_OPTION_SHORT") 
                        || args[i].equals("DECODE_OPTION_LONG")) {
                    index = i;
                    break;
                }
            }
            
            file1 = args[index + 1];
            file2 = args[index + 2];
        } catch (Exception ex) {
            System.err.println("Not enough tokens on command line.");
            System.exit(1);
        }
        
        byte[] inputData = readBytes(new File(file1));
        HuffmanDeserializer.Result result = 
                new HuffmanDeserializer().deserialize(inputData);
        byte[] originalData = new HuffmanDecoder()
                .decode(result.getEncodedText(),
                        result.getEncoderMap());
        
        writeBytes(originalData, new File(file2));
    }
        
    private static Set<String> getCommandLineOptions(String[] args) {
        Set<String> set = new HashSet<>();
        
        for (String arg : args) {
            set.add(arg);
        }
        
        return set;
    }
    
    private static void printHelpMessage() {
        String preamble = "usage: java -jar " + getThisJarName() + " ";
        int preambleLength = preamble.length();
        String indent = getIndent(preambleLength);
        
        StringBuilder sb = new StringBuilder();
        sb.append(preamble);
        
        sb.append("[")
          .append(HELP_OPTION_SHORT)
          .append(" | ")
          .append(HELP_OPTION_LONG)
          .append("]\n");
        
        sb.append(indent)
          .append("[")
          .append(VERSION_OPTION_SHORT)
          .append(" | ")
          .append(VERSION_OPTION_LONG)
          .append("]\n");
        
        sb.append(indent)
          .append("[")
          .append(ENCODE_OPTION_SHORT)
          .append(" | ")
          .append(ENCODE_OPTION_LONG)
          .append("] FILE\n");
        
        sb.append(indent)
          .append("[")
          .append(DECODE_OPTION_SHORT)
          .append(" | ")
          .append(DECODE_OPTION_LONG)
          .append("] FILE1 FILE2\n");
        
        sb.append("Where:\n");
        
        sb.append(HELP_OPTION_SHORT)
          .append(", ")
          .append(HELP_OPTION_LONG)
          .append("     Prints this message and exits.\n");
        
        sb.append(VERSION_OPTION_SHORT)
          .append(", ")
          .append(VERSION_OPTION_LONG)
          .append("  Prints the version info and exits.\n");
        
        sb.append(ENCODE_OPTION_SHORT)
          .append(", ")
          .append(ENCODE_OPTION_LONG)
          .append("   Encodes the text from standard input.\n");
        
        sb.append(DECODE_OPTION_SHORT)
          .append(", ")
          .append(DECODE_OPTION_LONG)
          .append("   Decodes the text from standard input.\n");
                
        System.out.println(sb.toString());
    }
    
    private static String getIndent(int preambleLength) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < preambleLength; ++i) {
            sb.append(' ');
        }
        
        return sb.toString();
    }
    
    private static void printVersion() {
        String msg = 
        "Huffman compressor tool, version 1.61 (Nov 19, 2016)\n" +
        "By Rodion \"rodde\" Efremov";
        
        System.out.println(msg);
    }
    
    private static String getThisJarName() {
        return new File(App.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()).getName();
    }
    
    private static void writeBytes(byte[] data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (IOException ex) {
            throw new RuntimeException(
            "ERROR: File IO failed while writing encoded data.", ex);
        } 
    }
    
    private static byte[] readBytes(File file) {
        try {
            Path path = Paths.get(file.getAbsolutePath());
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new RuntimeException(
            "ERROR: File IO failed while reading a binary file.", ex);
        }
    }
}
