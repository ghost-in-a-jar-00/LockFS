// Lock.java
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Lock{
    private static final int FILENAME_LEN = 8; // Number of characters will be 2x
    private static final String ENC_DIR_NAME = "enc";
    private static final String ENC_EXT = ".lkx";
    
    private static void encryptFiles(String dirPath, char[] password) throws IOException {
        File encDir = new File(ENC_DIR_NAME);
        encDir.mkdir();
        
        Path cwdEncrypt = Paths.get(dirPath);
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cwdEncrypt)) {
            for (Path file : stream) {
                String randName = SecureTools.genRand(FILENAME_LEN);
                String newFilename = randName + ENC_EXT;
                
                Path outPath = Paths.get(ENC_DIR_NAME, newFilename);
            
                if (Files.isRegularFile(file) && !file.toString().endsWith(ENC_EXT)){
                    try{
                        SecureTools.encryptFile(file.toString(), outPath.toString(), password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void encryptingDialog(String dirPath, char[] password){
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    
        final JDialog dialog = new JDialog(frame, "Encrypting", true);
        String cwd = System.getProperty("user.dir");
        Path outPath = Paths.get(cwd, ENC_DIR_NAME);
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
                encryptFiles(dirPath, password);
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

    public static void main(String[] args){
        char[] password = GuiTools.getPassword();
        
        if (password.length == 0){
            System.exit(0);
        }
        
        String pathDesc = "Select Path To Encrypt";
        String dirPath = GuiTools.selectDir(pathDesc);
        
        encryptingDialog(dirPath, password);
    }
}
