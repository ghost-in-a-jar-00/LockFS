// SecureTools.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

package lib;

import java.io.*;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

import java.util.Arrays;
import java.security.SecureRandom;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;


public class SecureTools{
    public static byte[] eraseBytes(byte[] byteArray){
        Arrays.fill(byteArray, (byte) 0);
        return byteArray;
    }
    
    public static char[] erasePassword(char[] passwordArray){
        Arrays.fill(passwordArray, '\0');
        return passwordArray;
    }
    
    public static String genRand(int length){
        SecureRandom rng = new SecureRandom();
        byte[] bytes = new byte[length];
        rng.nextBytes(bytes);
        
        StringBuilder hex = new StringBuilder();
        for (byte element: bytes){
            hex.append(String.format("%02x", element));
        }
        
        return hex.toString();
    }
    
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int ITERATIONS = 65536;
    private static final int KEY_SIZE = 256;
    private static final int TAG_LENGTH = 128;
    private static final int CHUNK_SIZE = 4096;
    
    public static void encryptFile(String inputFile, String outputFile, char[] passwordArray) throws Exception{
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        
        PBEKeySpec spec = new PBEKeySpec(passwordArray, salt, ITERATIONS, KEY_SIZE);
        erasePassword(passwordArray);
        
        byte[] keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        eraseBytes(keyBytes);
        
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
        
        String originalFilename = new File(inputFile).getName();
        byte[] filenameBytes = originalFilename.getBytes(StandardCharsets.UTF_8);
        if (filenameBytes.length > 65535) throw new IllegalArgumentException("Filename too long");
        
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher);
             FileInputStream fis = new FileInputStream(inputFile)) {
             
                fos.write(salt);
                fos.write(iv);
                
                fos.write((filenameBytes.length >> 8) & 0xFF);
                fos.write(filenameBytes.length & 0xFF);
                fos.write(filenameBytes);
             
                byte[] buffer = new byte[CHUNK_SIZE];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
                eraseBytes(buffer);
            }
        spec.clearPassword();
    }
    
    public static void decryptFile(String inputFile, String outputPath, char[] passwordArray) throws Exception{
        try (FileInputStream fis = new FileInputStream(inputFile)) {
        
            byte[] salt = new byte[SALT_LENGTH];
            if (fis.read(salt) != SALT_LENGTH) {
                throw new IOException("Unable to read salt from file");
            }
            
            byte[] iv = new byte[IV_LENGTH];
            if (fis.read(iv) != IV_LENGTH) {
                throw new IOException("Unable to read IV from file");
            }
            
            int nameLenHigh = fis.read();
            int nameLenLow = fis.read();
            if (nameLenHigh == -1 || nameLenLow == -1) {
                throw new IOException("Unable to read filename length");
            }
            int filenameLength = (nameLenHigh << 8) | nameLenLow;
            
            byte[] filenameBytes = new byte[filenameLength];
            if (fis.read(filenameBytes) != filenameLength) {
                throw new IOException("Unable to read filename bytes");
            }
            String originalFilename = new String(filenameBytes, StandardCharsets.UTF_8);
            
            Path outputFile = Paths.get(outputPath, originalFilename);
            
            PBEKeySpec spec = new PBEKeySpec(passwordArray, salt, ITERATIONS, KEY_SIZE);
            erasePassword(passwordArray);
            
            byte[] keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            eraseBytes(keyBytes);
                                                  
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            
            try (CipherInputStream cis = new CipherInputStream(fis, cipher);
                 FileOutputStream fos = new FileOutputStream(outputFile.toString())) {
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int bytesRead;
                    while ((bytesRead = cis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    eraseBytes(buffer);
             }
             spec.clearPassword();
        }
    }
}
