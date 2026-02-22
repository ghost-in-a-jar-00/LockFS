// Unlock.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

import lib.GuiTools;
import lib.SecureTools;

import java.io.IOException;
import java.io.File;
import java.nio.file.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class Unlock{
    private static final String UNLOCKED_DIR_NAME = "unlocked";
    private static final String ENC_EXT = ".lkx";

    private static void decryptFiles(String dirPath, char[] password) throws IOException {
        File decryptedDir = new File(UNLOCKED_DIR_NAME);
        decryptedDir.mkdir();
        
        Path cwdDecrypt = Paths.get(dirPath);
        
        Path outPath = Paths.get(UNLOCKED_DIR_NAME);
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cwdDecrypt)) {
            for (Path encFile : stream) {
                if (Files.isRegularFile(encFile) && encFile.toString().endsWith(ENC_EXT)){
                    try{
                        SecureTools.decryptFile(encFile.toString(), outPath.toString(), password);
                        Files.delete(encFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void decryptingDialog(String dirPath, char[] password){
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    
        final JDialog dialog = new JDialog(frame, "Decrypting", true);
        String cwd = System.getProperty("user.dir");
        Path outPath = Paths.get(cwd, UNLOCKED_DIR_NAME);
        JLabel label = new JLabel("<html>This will close once finished<br><br>" +
                                  "Your files can be found in " + outPath.toString() + "</html>");
        System.out.println("Your files can be found in " + outPath.toString());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                decryptFiles(dirPath, password);
                return null;
            }
            
            @Override
            protected void done(){
                dialog.dispose();
                System.exit(0);
            }
        };
        worker.execute();
        dialog.setVisible(true);
    }
    
    public static void main (String[] args){
        char[] password = GuiTools.getDecrypt();
        
        if (password.length == 0){
            System.exit(0);
        }
        
        String pathDesc = "Select Path To Decrypt";
        String dirPath = GuiTools.selectDir(pathDesc);
        
        decryptingDialog(dirPath, password);
    }
}
