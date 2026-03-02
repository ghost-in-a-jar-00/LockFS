// FormatHelpers.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

package lib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FormatHelpers{
    public static void writeFixedString(FileOutputStream fos, String text, int allocLen, String fieldLabel) throws IOException{
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > allocLen){
            throw new IllegalArgumentException("String too long for " + String.valueOf(allocLen) + " bytes." + "\n" + 
                                               "Cannot write " + fieldLabel + ".\n");
        }
        fos.write(bytes);
        for (int i = bytes.length; i < allocLen; i++){
            fos.write(0);
        }
    }
    
    public static String getFixedString(FileInputStream fis, int allocLen, String fieldLabel) throws IOException{
        byte[] buffer = new byte[allocLen];
        int read = fis.read(buffer);
        if (read != allocLen){
            throw new EOFException("Unexpected end of file while reading fixed-length string " + fieldLabel + " field");
        }
        int strLen = allocLen;
        while (strLen > 0 && buffer[strLen-1] == 0){
            strLen--;
        }
        return new String(buffer, 0, strLen, StandardCharsets.UTF_8);
    }
    
    public static int getLongInt(FileInputStream fis, String fieldLabel) throws IOException{
        int byte1 = fis.read();
        int byte2 = fis.read();
        int byte3 = fis.read();
        int byte4 = fis.read();
        if ((byte1|byte2|byte3|byte4) < 0){
            throw new EOFException("Unexpected end of file while reading 4-byte integer " + fieldLabel + " field");
        }
        return (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4;
    }
    
    public static int getShortInt(FileInputStream fis, String fieldLabel) throws IOException{
        int byte1 = fis.read();
        int byte2 = fis.read();
        if ((byte1|byte2) < 0){
            throw new EOFException("Unexpected end of file while reading 2-byte integer "  + fieldLabel + " field");
        }
        return (byte1 << 8) | byte2;
    }
    
    public static int getSingleInt(FileInputStream fis, String fieldLabel) throws IOException{
        int byteInt = fis.read();
        if (byteInt < 0){
            throw new EOFException("Unexpected end of file while reading 1-byte integer " + fieldLabel + " field");
        }
        return byteInt;
    }
}
