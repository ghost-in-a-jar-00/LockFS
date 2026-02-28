// SecureTools.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

package lib;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

import java.util.Arrays;
import java.security.SecureRandom;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.ByteBuffer;
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
    private static final int KEY_SIZE = 256;
    private static final int TAG_LENGTH = 128;
    private static final int CHUNK_SIZE = 4096;
    
    private static final int A2ID_MEMORY = 65536;
    private static final int A2ID_THREADS = 2;
    private static final int A2ID_ITERATIONS = 4;
    
    public static void encryptFile(Path cwdEncrypt, String inputFile, String outputFile, char[] passwordArray) throws Exception{
        Path inputPath = Paths.get(inputFile);
        Path relativePath = cwdEncrypt.relativize(inputPath);
        String filePath = relativePath.toString();
        byte[] pathBytes = filePath.getBytes(StandardCharsets.UTF_8);
        
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                                               .withSalt(salt)
                                               .withParallelism(A2ID_THREADS)
                                               .withMemoryAsKB(A2ID_MEMORY)
                                               .withIterations(A2ID_ITERATIONS);
                                               
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());
        
        byte[] passwordBytes = new byte[passwordArray.length];
        for (int i = 0; i < passwordArray.length; i++){
            passwordBytes[i] = (byte) passwordArray[i];
        }
        
        byte[] keyBytes = new byte[KEY_SIZE/8];
        generator.generateBytes(passwordBytes, keyBytes);
        
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        
        eraseBytes(passwordBytes);
        eraseBytes(keyBytes);
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)){
             
                fos.write(salt);
                
                if (pathBytes.length > 65535) {
                    throw new IllegalArgumentException("Path too long for 2-byte length");
                }
                
                byte[] pathIV = new byte[IV_LENGTH];
                new SecureRandom().nextBytes(pathIV);
                Cipher pathCipher = Cipher.getInstance("AES/GCM/NoPadding");
                pathCipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, pathIV));
                byte[] encryptedPath = pathCipher.doFinal(pathBytes);
                
                fos.write(pathIV);
             
                fos.write((encryptedPath.length >> 8) & 0xFF);
                fos.write(encryptedPath.length & 0xFF);
                fos.write(encryptedPath);
                
                eraseBytes(pathBytes);
                
                byte[] buffer = new byte[CHUNK_SIZE];
                int read;
                
                while ((read = fis.read(buffer)) != -1) {
                    byte[] iv = new byte[IV_LENGTH];
                    new SecureRandom().nextBytes(iv);
                    fos.write(iv);
                    
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
                    
                    byte[] encryptedChunk = cipher.doFinal(buffer, 0, read);
                    fos.write(encryptedChunk);
                }                
                eraseBytes(buffer);
             }
    }
    
    public static void decryptFile(String inputFile, String outputPath, char[] passwordArray) throws Exception{
        try (FileInputStream fis = new FileInputStream(inputFile)){
             
                byte[] salt = new byte[SALT_LENGTH];
                fis.readNBytes(salt, 0, SALT_LENGTH);
                
                Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                                                       .withSalt(salt)
                                                       .withParallelism(A2ID_THREADS)
                                                       .withMemoryAsKB(A2ID_MEMORY)
                                                       .withIterations(A2ID_ITERATIONS);
                                                       
                Argon2BytesGenerator generator = new Argon2BytesGenerator();
                generator.init(builder.build());
                
                byte[] passwordBytes = new byte[passwordArray.length];
                for (int i = 0; i < passwordArray.length; i++){
                    passwordBytes[i] = (byte) passwordArray[i];
                }
                
                byte[] keyBytes = new byte[KEY_SIZE/8];
                generator.generateBytes(passwordBytes, keyBytes);
                
                SecretKey key = new SecretKeySpec(keyBytes, "AES");
                
                eraseBytes(passwordBytes);
                eraseBytes(keyBytes);
                
                byte[] pathIV = new byte[IV_LENGTH];
                if (fis.read(pathIV) != IV_LENGTH){
                    throw new IOException("Unexpected EOF reading path IV");
                }
                
                int highByte = fis.read();
                int lowByte = fis.read();
                if (highByte == -1 || lowByte == -1) {
                    throw new IOException("Unexpected EOF while reading path length");
                }
                int pathLength = (highByte << 8) | lowByte;
                byte[] encPathBytes = new byte[pathLength];
                
                if (fis.readNBytes(encPathBytes, 0, pathLength) != pathLength){
                    throw new IOException("Unexpected EOF reading encrypted path bytes");
                }
                
                Cipher pathCipher = Cipher.getInstance("AES/GCM/NoPadding");
                pathCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, pathIV));
                byte[] pathBytes = pathCipher.doFinal(encPathBytes);
                String originalPath = new String(pathBytes, StandardCharsets.UTF_8);
                Path embeddedPath = Paths.get(outputPath, originalPath);
                Files.createDirectories(embeddedPath.getParent());
                File outputFile = embeddedPath.toFile();             
                
                try(FileOutputStream fos = new FileOutputStream(outputFile)){
                    byte[] buffer = new byte[CHUNK_SIZE];
                    
                    while (true){
                        byte[] iv = new byte[IV_LENGTH];
                        int ivBytes = fis.read(iv);
                        if (ivBytes == -1){
                            break;
                        }
                        if (ivBytes != IV_LENGTH){
                            throw new IOException("Unexpected EOF reading chunk IV");
                        }
                        
                        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
                        
                        byte[] encryptedChunk = fis.readNBytes(CHUNK_SIZE + TAG_LENGTH/8);
                        if (encryptedChunk.length == 0){
                            break;
                        }
                        byte[] decrypted = cipher.doFinal(encryptedChunk);
                        fos.write(decrypted);
                        
                        eraseBytes(buffer);
                    }
                }
        }
    }
    
    public static byte[] encryptIO(byte[] data, char[] passwordArray) throws Exception{
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                                               .withSalt(salt)
                                               .withParallelism(A2ID_THREADS)
                                               .withMemoryAsKB(A2ID_MEMORY)
                                               .withIterations(A2ID_ITERATIONS);
                                               
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());
        
        byte[] passwordBytes = new byte[passwordArray.length];
        for (int i = 0; i < passwordArray.length; i++){
            passwordBytes[i] = (byte) passwordArray[i];
        }
        
        byte[] keyBytes = new byte[KEY_SIZE/8];
        generator.generateBytes(passwordBytes, keyBytes);
        
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        
        eraseBytes(passwordBytes);
        eraseBytes(keyBytes);
            
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
        
        byte[] encData = cipher.doFinal(data);
        
        ByteBuffer buffer = ByteBuffer.allocate(salt.length + iv.length + encData.length);
        buffer.put(salt);
        buffer.put(iv);
        buffer.put(encData);
        return buffer.array();
    }
    
    public static byte[] decryptIO(byte[] encData, char[] passwordArray) throws Exception{
        ByteBuffer buffer = ByteBuffer.wrap(encData);
        
        byte[] salt = new byte[SALT_LENGTH];
        buffer.get(salt);
        
        byte[] iv = new byte[IV_LENGTH];
        buffer.get(iv);
        
        byte[] ciphertext = new byte[buffer.remaining()];
        buffer.get(ciphertext);
        
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                                               .withSalt(salt)
                                               .withParallelism(A2ID_THREADS)
                                               .withMemoryAsKB(A2ID_MEMORY)
                                               .withIterations(A2ID_ITERATIONS);
                                               
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());
        
        byte[] passwordBytes = new byte[passwordArray.length];
        for (int i = 0; i < passwordArray.length; i++){
            passwordBytes[i] = (byte) passwordArray[i];
        }
        
        byte[] keyBytes = new byte[KEY_SIZE/8];
        generator.generateBytes(passwordBytes, keyBytes);
        
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        
        eraseBytes(passwordBytes);
        eraseBytes(keyBytes);
            
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
        return cipher.doFinal(ciphertext);
    }
}
