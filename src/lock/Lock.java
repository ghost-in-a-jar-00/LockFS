// Lock.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

import lib.GuiTools;
import lib.SecureTools;

import java.io.IOException;
import java.io.File;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

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
        
        try (Stream<Path> paths = Files.walk(cwdEncrypt)) {
            paths
            .filter(Files::isRegularFile)
            .filter(filePath -> !filePath.toString().endsWith(ENC_EXT))
            .forEach(file -> {
                String randName = SecureTools.genRand(FILENAME_LEN);
                String newFilename = randName + ENC_EXT;
                Path outPath = Paths.get(ENC_DIR_NAME, newFilename);

                try{
                    SecureTools.encryptFile(cwdEncrypt, file.toString(), outPath.toString(), password);
                    Files.delete(file);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            
          SecureTools.erasePassword(password);
            
            Path dir = Path.of(dirPath);
            if (Files.exists(dir)){
                Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                try{
                    Files.delete(path);
                }catch (IOException e){
                    e.printStackTrace();
                }
                });
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
