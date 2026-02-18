// GuiTools.java
// Written by Ghost In A Jar
// This is licensed under the MIT License

package lib;
import javax.swing.*;
import java.util.function.Consumer;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.awt.Font;

public class GuiTools{
    private static final String FONT_STYLE_TEXT = "Arial";
    private static final int FONT_SIZE_TEXT = 15;

    public static void showText(String text){
        JFrame frame = new JFrame();
        frame.setSize(500, 400);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_STYLE_TEXT, Font.PLAIN, FONT_SIZE_TEXT));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        frame.add(label);
        frame.setVisible(true);
    }

    public static char[] getText(String title, String purpose){
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(1, 1, 5, 5));
        JTextField textField = new JTextField();
        
        panel.add(new JLabel(purpose));
        panel.add(textField);
        
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(panel);
        
        String[] options = {"Submit"};
        
        
        int result = JOptionPane.showOptionDialog(frame, wrapper, title,
                                                  JOptionPane.DEFAULT_OPTION,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  null, options, options[0]);
                                                      
        if (result != 0) {
            frame.dispose();
            return new char[0];
        }
            
        return textField.getText().toCharArray();
    }

    public static char[] getPassword(){
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JPasswordField passField = new JPasswordField(16);
        JPasswordField confirmField = new JPasswordField(16);
        JCheckBox showPassword = new JCheckBox("Show Password");
        
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmField);
        
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(panel);
        wrapper.add(Box.createRigidArea(new Dimension(0, 10)));
        showPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(showPassword);
        
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passField.setEchoChar((char) 0);
                confirmField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('*');
                confirmField.setEchoChar('*');
            }
        });
        
        String[] options = {"Submit", "Cancel"};
        
        while (true) {
            int result = JOptionPane.showOptionDialog(frame, wrapper, "Set Password",
                                                      JOptionPane.DEFAULT_OPTION,
                                                      JOptionPane.PLAIN_MESSAGE,
                                                      null, options, options[0]);
                                                      
            if (result != 0) {
                frame.dispose();
                return new char[0];
            }
            
            char[] password = passField.getPassword();
            char[] confirm = confirmField.getPassword();
            
            if (password.length == 0 || confirm.length == 0) {
                JOptionPane.showMessageDialog(frame, "Please fill in both fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            if (!Arrays.equals(password, confirm)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Warning", JOptionPane.WARNING_MESSAGE);
                Arrays.fill(password, '\0');
                Arrays.fill(confirm, '\0');
                continue;
            }
            
            Arrays.fill(confirm, '\0');
            return password;
        }
    }
    
    public static String selectDir(String task){
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(task);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        
        int result = chooser.showOpenDialog(frame);
        frame.dispose();
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            String path = selectedDir.getAbsolutePath();
            return path;
        }
        else{
            return null;
        }
    }
}
