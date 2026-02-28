// RevealName.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

package org.lockfs;

import lib.GuiTools;
import lib.SecureTools;
import lib.FileOp;
import java.io.File;
import java.nio.file.Path;
import java.io.IOException;

public class RevealName{
    private static final char SEPARATOR = ','; //For file usage, CSV format (name,alias)
    private static final String DB_EXT_NAME = ".vnm";

    private static void prepDisplay(char[] nameMeta){
        String rawText = new String(nameMeta);
        String sepStr = String.valueOf(SEPARATOR);
        String[] splitText = rawText.split(sepStr); 
        String text = splitText[1] + ": " + splitText[0];
        GuiTools.showText(text);
    }

    public static void main(String[] args) throws Exception{
        char[] password = GuiTools.getDecrypt();
        
        String dirTask = "Choose Vault";
        String vaultDB = GuiTools.selectDir(dirTask);
        
        File vaultPath = new File(vaultDB);
        
        if (vaultPath.exists() && vaultPath.isDirectory()){
            File[] files = vaultPath.listFiles((isDir, name) -> name.endsWith(DB_EXT_NAME));
            
            if (files != null && files.length > 0) {
                File focusFile = files[0];
                Path focusPath = focusFile.toPath();
                try{
                    char[] nameMeta = FileOp.peekVault(focusPath, password);
                    SecureTools.erasePassword(password);
                    prepDisplay(nameMeta);
                }catch (Exception e){
                    System.err.println("Failed to read vault: " + e.getMessage());
                    System.exit(1);
                }
                
            }
        }else{
            throw new IOException(vaultPath.getPath() + " not found");
        }
    }
}
