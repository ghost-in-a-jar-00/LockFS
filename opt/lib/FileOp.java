// FileOp.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

package lib;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOp{
    private static final char SEPARATOR = ','; //For file usage, CSV format (name,alias)
    private static final String DB_EXT_NAME = ".vnm";
    private static final String VAULT_MAIN = "VAULT";
    private static final int VAULT_ALIAS_NAME_LEN = 8;
    
    public static char[] peekVault(Path pathDB, char[] password) throws Exception{        
        try{
            File fileDB = pathDB.toFile();
            
            if (!fileDB.exists()) {
                throw new IOException(pathDB + " not found: ");
            }
            
            byte[] encMeta = Files.readAllBytes(pathDB);
            
            byte[] details = SecureTools.decryptIO(encMeta, password);
            
            if (details.length % 2 != 0) {
                throw new IOException("Invalid byte length for char conversion");
            }
            
            char[] nameMeta = new char[details.length / 2];
            
            for (int i = 0; i < nameMeta.length; i++) {
                nameMeta[i] = (char) (((details[i * 2] & 0xFF) << 8) | (details[i * 2 + 1] & 0xFF));
            }
            
            SecureTools.eraseBytes(details);
            
            return nameMeta;
            
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }        
    }
    
    private static void storeAlias(String alias, char[] vaultName, char[] password) throws Exception{
        String vaultDB = alias + DB_EXT_NAME;
        Path pathDB = Paths.get(alias, vaultDB);
        char[] aliasChar = alias.toCharArray();
        
        char[] nameMeta = new char[vaultName.length + 1 + aliasChar.length]; // 1 refers to SEPARATOR
        System.arraycopy(vaultName, 0, nameMeta, 0, vaultName.length);
        SecureTools.erasePassword(vaultName);
        nameMeta[vaultName.length] = SEPARATOR;
        System.arraycopy(aliasChar, 0, nameMeta, vaultName.length + 1, aliasChar.length);
        
        byte[] details = new byte[nameMeta.length * 2];
        
        for (int i = 0; i < nameMeta.length; i++) {
            details[i * 2] = (byte) ((nameMeta[i] >> 8) & 0xFF);
            details[i * 2 + 1] = (byte) (nameMeta[i] & 0xFF);
        }
        
        SecureTools.erasePassword(nameMeta);
        
        byte[] encMeta = SecureTools.encryptIO(details, password);
        
        File fileDB = pathDB.toFile();
        
        try (FileOutputStream fos = new FileOutputStream(fileDB)) {
            fos.write(encMeta);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void createVault(char[] vaultName, char[] password) throws Exception{
        String alias = SecureTools.genRand(VAULT_ALIAS_NAME_LEN);
        Path vault = Paths.get(alias, VAULT_MAIN);
        
        try{
            if (Files.notExists(vault)){
                Files.createDirectories(vault);
                storeAlias(alias, vaultName, password);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
