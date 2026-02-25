// GenVault.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

import lib.GuiTools;
import lib.SecureTools;
import lib.FileOp;

public class GenVault{
    public static void main(String[] args) throws Exception{
        char[] password = GuiTools.getPassword();
    
        String title = "Enter Vault Name";
        String purpose = "Vault name: ";
        char[] vaultName = GuiTools.getText(title, purpose);
        
        FileOp.createVault(vaultName, password);
        SecureTools.erasePassword(password);
        
        System.exit(0);
    }
}
