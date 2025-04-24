package client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JCheckBox;
import java.awt.Cursor;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.LineBorder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class clientUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel pnbtn;
    private JTextArea chattextarea;
    private JLabel lbsent;
    private JLabel lbemoji;
    private JLabel lbfile;
    private JLabel lbimg;
    private JLabel lbimgsecret;
    private JLabel lbvoice;
    private JCheckBox cboxmahoa;
    private String username;
    private List<JPanel> messagePanels;
    private JScrollPane chatScrollPane;
    private JPanel chatContentPanel;
    private static final String[] EMOJIS = {"😊", "😂", "😍", "😢", "😡", "👍", "👎", "❤️", "🎉", "🔥"};
    private static final int CHAT_AREA_HEIGHT = 890;
    private boolean isRecording = false;
    private TargetDataLine line;
    private ByteArrayOutputStream audioOutputStream;
    private File tempAudioFile;
    private JDialog tempVoiceDialog;
    private controllclient controller;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    clientUI frame = new clientUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public clientUI() {
        messagePanels = new ArrayList<>();

        username = JOptionPane.showInputDialog(this, "Vui lòng nhập tên của bạn:", "Nhập tên", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous";
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 0, 870, 1024);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setResizable(false);
        chatContentPanel = new JPanel();
        chatContentPanel.setBackground(new Color(100, 126, 147));
        chatContentPanel.setLayout(null);

        chatScrollPane = new JScrollPane(chatContentPanel);
        chatScrollPane.setBounds(0, 0, 861, 890);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        contentPane.add(chatScrollPane);

        pnbtn = new JPanel();
        pnbtn.setBackground(new Color(255, 255, 255));
        pnbtn.setBounds(0, 890, 858, 100);
        contentPane.add(pnbtn);
        pnbtn.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 771, 50);
        pnbtn.add(scrollPane);

        chattextarea = new JTextArea();
        scrollPane.setViewportView(chattextarea);
        chattextarea.setLineWrap(true);
        chattextarea.setText("");
        chattextarea.setForeground(Color.BLACK);

        JPanel pnsent = new JPanel();
        pnsent.setBounds(771, 0, 90, 50);
        pnbtn.add(pnsent);
        pnsent.setLayout(null);

        lbsent = new JLabel("");
        lbsent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbsent.setIcon(new ImageIcon(clientUI.class.getResource("/img/sent.png")));
        lbsent.setBounds(0, 0, 90, 50);
        pnsent.add(lbsent);

        JPanel pnemoji = new JPanel();
        pnemoji.setLayout(null);
        pnemoji.setBounds(0, 50, 90, 50);
        pnbtn.add(pnemoji);

        lbemoji = new JLabel("");
        lbemoji.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbemoji.setIcon(new ImageIcon(clientUI.class.getResource("/img/emoji.png")));
        lbemoji.setBounds(0, 0, 90, 50);
        pnemoji.add(lbemoji);

        JPanel pnfile = new JPanel();
        pnfile.setLayout(null);
        pnfile.setBounds(150, 50, 90, 50);
        pnbtn.add(pnfile);

        lbfile = new JLabel("");
        lbfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbfile.setIcon(new ImageIcon(clientUI.class.getResource("/img/file.png")));
        lbfile.setBounds(0, 0, 90, 50);
        pnfile.add(lbfile);

        JPanel pnimg = new JPanel();
        pnimg.setLayout(null);
        pnimg.setBounds(300, 50, 90, 50);
        pnbtn.add(pnimg);

        lbimg = new JLabel("");
        lbimg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbimg.setIcon(new ImageIcon(clientUI.class.getResource("/img/img.png")));
        lbimg.setBounds(0, 0, 90, 50);
        pnimg.add(lbimg);

        JPanel pnimgsecret = new JPanel();
        pnimgsecret.setLayout(null);
        pnimgsecret.setBounds(450, 50, 90, 50);
        pnbtn.add(pnimgsecret);

        lbimgsecret = new JLabel("");
        lbimgsecret.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbimgsecret.setIcon(new ImageIcon(clientUI.class.getResource("/img/secretfileimg.png")));
        lbimgsecret.setBounds(0, 0, 90, 50);
        pnimgsecret.add(lbimgsecret);

        JPanel pnmahoa = new JPanel();
        pnmahoa.setBackground(new Color(255, 255, 255));
        pnmahoa.setLayout(null);
        pnmahoa.setBounds(771, 50, 90, 50);
        pnbtn.add(pnmahoa);

        cboxmahoa = new JCheckBox("Mã hoá");
        cboxmahoa.setBackground(new Color(255, 255, 255));
        cboxmahoa.setFont(new Font("Arial", Font.BOLD, 16));
        cboxmahoa.setBounds(0, 0, 90, 50);
        pnmahoa.add(cboxmahoa);

        JPanel pnvoice = new JPanel();
        pnvoice.setLayout(null);
        pnvoice.setBounds(600, 50, 90, 50);
        pnbtn.add(pnvoice);

        lbvoice = new JLabel("");
        lbvoice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbvoice.setIcon(new ImageIcon(clientUI.class.getResource("/img/voice.png")));
        lbvoice.setBounds(0, 0, 90, 50);
        pnvoice.add(lbvoice);

        controller = new controllclient(this);
    }

    public JPanel getPnbtn() { return pnbtn; }
    public JTextArea getChattextarea() { return chattextarea; }
    public JLabel getLbsent() { return lbsent; }
    public JLabel getLbemoji() { return lbemoji; }
    public JLabel getLbfile() { return lbfile; }
    public JLabel getLbimg() { return lbimg; }
    public JLabel getLbimgsecret() { return lbimgsecret; }
    public JLabel getLbvoice() { return lbvoice; }
    public JCheckBox getCboxmahoa() { return cboxmahoa; }
    public String getUsername() { return username; }

    public JPanel createMePanel(String message) {
        JPanel newMePn = new JPanel();
        newMePn.setBackground(new Color(100, 126, 147));
        newMePn.setForeground(new Color(100, 126, 147));
        newMePn.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(332, 11, 519, 133);
        newMePn.add(scrollPane);

        JTextArea messageArea = new JTextArea();
        scrollPane.setViewportView(messageArea);
        messageArea.setLineWrap(true);
        messageArea.setForeground(new Color(0, 0, 0));
        messageArea.setBackground(new Color(206, 244, 180));
        messageArea.setText(message);

        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setForeground(new Color(255, 255, 255));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setBounds(332, 145, 100, 20);
        newMePn.add(timeLabel);

        chatContentPanel.add(newMePn);
        return newMePn;
    }

    public JPanel createYouPanel(String message, boolean isEncrypted) {
        JPanel newYouPn = new JPanel();
        newYouPn.setBackground(new Color(100, 126, 147));
        newYouPn.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 11, 519, 133);
        newYouPn.add(scrollPane);

        JTextArea messageArea = new JTextArea();
        scrollPane.setViewportView(messageArea);
        messageArea.setLineWrap(true);
        messageArea.setForeground(new Color(0, 0, 0));
        messageArea.setBackground(new Color(206, 244, 180));
        messageArea.setText(message);

        JButton btnsearchmahoa = new JButton("");
        btnsearchmahoa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnsearchmahoa.setIcon(new ImageIcon(clientUI.class.getResource("/img/searchicon.png")));
        btnsearchmahoa.setFont(new Font("Tahoma", Font.PLAIN, 11));
        btnsearchmahoa.setBounds(474, 142, 54, 23);
        if (isEncrypted) {
            btnsearchmahoa.addActionListener(e -> {
                String decryptedMessage = showDecryptInputDialog(message);
                if (decryptedMessage != null) {
                    JOptionPane.showMessageDialog(this, "Tin nhắn gốc: " + decryptedMessage, "Giải mã", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        newYouPn.add(btnsearchmahoa);

        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setForeground(new Color(255, 255, 255));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setBounds(429, 145, 100, 20);
        newYouPn.add(timeLabel);

        chatContentPanel.add(newYouPn);
        return newYouPn;
    }

    public JPanel createFilePanel(String fileName, String filePath) {
        JPanel newFilePn = new JPanel();
        newFilePn.setBackground(new Color(100, 126, 147));
        newFilePn.setLayout(null);

        JLabel lbnamefile = new JLabel(fileName);
        lbnamefile.setForeground(new Color(0, 0, 255));
        lbnamefile.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbnamefile.setBounds(10, 11, 505, 40);
        newFilePn.add(lbnamefile);

        JButton btndownloadfile = new JButton("");
        btndownloadfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btndownloadfile.setIcon(new ImageIcon(clientUI.class.getResource("/img/download.png")));
        btndownloadfile.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btndownloadfile.setBounds(461, 60, 54, 23);
        btndownloadfile.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this, "Bạn có muốn tải file này không?", "Xác nhận tải file", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    File sourceFile = new File(filePath);
                    File destFile = new File(System.getProperty("user.home") + "/Downloads/" + fileName);
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "File đã được tải về: " + destFile.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tải file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        newFilePn.add(btndownloadfile);

        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setForeground(new Color(255, 255, 255));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setBounds(10, 90, 100, 20);
        newFilePn.add(timeLabel);

        chatContentPanel.add(newFilePn);
        return newFilePn;
    }

    public JPanel createImgPanel(String filePath, String secretMessage) {
        JPanel newImgPn = new JPanel();
        newImgPn.setBackground(new Color(100, 126, 147));
        newImgPn.setLayout(null);

        JPanel showimg = new JPanel();
        showimg.setBorder(new LineBorder(new Color(0, 0, 0)));
        showimg.setBounds(10, 11, 178, 178);

        try {
            ImageIcon imageIcon = new ImageIcon(filePath);
            JLabel imageLabel = new JLabel(imageIcon);
            showimg.add(imageLabel);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Không thể hiển thị ảnh");
            showimg.add(errorLabel);
        }

        newImgPn.add(showimg);

        JButton btndownloadimg = new JButton("");
        btndownloadimg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btndownloadimg.setIcon(new ImageIcon(clientUI.class.getResource("/img/download.png")));
        btndownloadimg.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btndownloadimg.setBounds(189, 166, 54, 23);
        btndownloadimg.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this, "Bạn có muốn tải ảnh này không?", "Xác nhận tải ảnh", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    File sourceFile = new File(filePath);
                    File destFile = new File(System.getProperty("user.home") + "/Downloads/" + sourceFile.getName());
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "Ảnh đã được tải về: " + destFile.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tải ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        newImgPn.add(btndownloadimg);

        JButton btnsearchsecretimg = new JButton("");
        btnsearchsecretimg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnsearchsecretimg.setIcon(new ImageIcon(clientUI.class.getResource("/img/searchicon.png")));
        btnsearchsecretimg.setFont(new Font("Tahoma", Font.PLAIN, 11));
        btnsearchsecretimg.setBounds(189, 142, 54, 23);
        if (secretMessage != null && !secretMessage.isEmpty()) {
            btnsearchsecretimg.addActionListener(e -> {
                String decryptedMessage = showDecryptInputDialog(secretMessage);
                if (decryptedMessage != null) {
                    JOptionPane.showMessageDialog(this, "Tin nhắn bí mật: " + decryptedMessage, "Tin nhắn bí mật", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        newImgPn.add(btnsearchsecretimg);

        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setForeground(new Color(255, 255, 255));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setBounds(250, 170, 100, 20);
        newImgPn.add(timeLabel);

        chatContentPanel.add(newImgPn);
        return newImgPn;
    }

    public JPanel createVoicePanel(String filePath) {
        JPanel newVoicePn = new JPanel();
        newVoicePn.setBackground(new Color(100, 126, 147));
        newVoicePn.setLayout(null);

        JLabel lbVoiceLabel = new JLabel("Tin nhắn thoại");
        lbVoiceLabel.setForeground(new Color(0, 0, 255));
        lbVoiceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbVoiceLabel.setBounds(10, 11, 505, 40);
        newVoicePn.add(lbVoiceLabel);

        JButton btnPlay = new JButton("Phát");
        btnPlay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPlay.setBounds(461, 60, 90, 30);
        btnPlay.addActionListener(e -> playAudio(filePath));
        btnPlay.setBackground(new Color(255, 255, 255));
        newVoicePn.add(btnPlay);

        JButton btnDownload = new JButton("");
        btnDownload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDownload.setIcon(new ImageIcon(clientUI.class.getResource("/img/download.png")));
        btnDownload.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnDownload.setBounds(561, 60, 54, 23);
        btnDownload.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this, "Bạn có muốn tải file này không?", "Xác nhận tải file", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    File sourceFile = new File(filePath);
                    File destFile = new File(System.getProperty("user.home") + "/Downloads/" + sourceFile.getName());
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "File đã được tải về: " + destFile.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tải file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        newVoicePn.add(btnDownload);

        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setForeground(new Color(255, 255, 255));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setBounds(10, 90, 100, 20);
        newVoicePn.add(timeLabel);

        chatContentPanel.add(newVoicePn);
        return newVoicePn;
    }

    public void showMessagePanel(JPanel panel) {
        messagePanels.add(panel);
        updatePanelPositions();
    }

    private void updatePanelPositions() {
        int panelHeight = 200;
        int totalPanels = messagePanels.size();
        int totalHeight = totalPanels * panelHeight;

        int startY = 0;
        for (int i = 0; i < totalPanels; i++) {
            JPanel panel = messagePanels.get(i);
            panel.setBounds(0, startY + (i * panelHeight), 861, panelHeight);
            panel.setVisible(true);
        }

        chatContentPanel.setPreferredSize(new java.awt.Dimension(861, totalHeight));
        chatContentPanel.revalidate();
        chatContentPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum());
        });
    }

    public File chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public File chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public File saveTempFile(String fileName, byte[] fileContent) {
        try {
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
            Files.write(tempFile.toPath(), fileContent);
            return tempFile;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu file tạm thời: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void showEmojiPanel() {
        JDialog emojiDialog = new JDialog(this, "Chọn Emoji", true);
        emojiDialog.getContentPane().setLayout(new java.awt.GridLayout(0, 5, 5, 5));
        emojiDialog.setSize(300, 200);
        emojiDialog.setLocationRelativeTo(this);

        for (String emoji : EMOJIS) {
            JButton emojiButton = new JButton(emoji);
            emojiButton.setFont(new Font("Tohoma", Font.PLAIN, 11));
            emojiButton.setBackground(new Color(255, 255, 255));
            emojiButton.addActionListener(e -> {
                String currentText = chattextarea.getText();
                chattextarea.setText(currentText + emoji);
                chattextarea.setForeground(Color.BLACK);
                emojiDialog.dispose();
            });
            emojiDialog.getContentPane().add(emojiButton);
        }

        emojiDialog.setVisible(true);
    }

    public String showSecretMessageInput() {
        JTextField secretField = new JTextField(20);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Nhập tin nhắn bí mật:"));
        panel.add(secretField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tin nhắn bí mật", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String message = secretField.getText();
            if (!message.isEmpty()) {
                return encryptSecretMessage(message);
            }
        }
        return null;
    }

    private String encryptSecretMessage(String message) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            encrypted.append((char) (c + 32));
        }
        return encrypted.toString();
    }

    private String decryptSecretMessage(String message, int key) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            decrypted.append((char) (c - key));
        }
        return decrypted.toString();
    }

    private String showDecryptInputDialog(String encryptedMessage) {
        JTextField keyField = new JTextField(5);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Nhập mã giải mã:"));
        panel.add(keyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Giải mã tin nhắn", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int key = Integer.parseInt(keyField.getText().trim());
                String decryptedMessage = decryptSecretMessage(encryptedMessage, key);
                if (key == 32) {
                    return decryptedMessage;
                } else {
                    JOptionPane.showMessageDialog(this, "Kết quả giải mã với mã " + key + ": " + decryptedMessage, 
                        "Mã không đúng", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập một số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }

    public String encryptMessage(String message) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            encrypted.append((char) (c + 1));
        }
        return encrypted.toString();
    }

    private String decryptMessage(String message) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            decrypted.append((char) (c - 1));
        }
        return decrypted.toString();
    }

    public void startRecording() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            audioOutputStream = new ByteArrayOutputStream();
            isRecording = true;

            new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (isRecording) {
                    int bytesRead = line.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        audioOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }).start();

            JOptionPane.showMessageDialog(this, "Đang ghi âm... Nhấn OK để dừng.", "Ghi âm", JOptionPane.INFORMATION_MESSAGE);
            stopRecording();
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, "Không thể truy cập microphone: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public File stopRecording() {
        if (!isRecording) return null;

        isRecording = false;
        line.stop();
        line.close();

        try {
            byte[] audioData = audioOutputStream.toByteArray();
            tempAudioFile = new File(System.getProperty("java.io.tmpdir") + "/voice_message_" + System.currentTimeMillis() + ".wav");
            AudioInputStream audioInputStream = new AudioInputStream(
                new ByteArrayInputStream(audioData), new AudioFormat(44100, 16, 1, true, true), audioData.length / 2);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, tempAudioFile);
            audioOutputStream.close();

            showTempVoicePanel(tempAudioFile);
            return tempAudioFile;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu file âm thanh: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi phát âm thanh: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTempVoicePanel(File audioFile) {
        tempVoiceDialog = new JDialog(this, "Xem lại tin nhắn thoại", true);
        tempVoiceDialog.setSize(400, 150);
        tempVoiceDialog.setLocationRelativeTo(this);
        tempVoiceDialog.getContentPane().setLayout(null);

        JPanel tempPanel = new JPanel();
        tempPanel.setBackground(new Color(255, 255, 255));
        tempPanel.setBounds(0, 0, 400, 150);
        tempPanel.setLayout(null);

        JLabel lbVoiceLabel = new JLabel("Đoạn ghi âm của bạn");
        lbVoiceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbVoiceLabel.setBounds(10, 10, 380, 30);
        tempPanel.add(lbVoiceLabel);

        JButton btnPlay = new JButton("Nghe");
        btnPlay.setBounds(10, 50, 80, 30);
        btnPlay.addActionListener(e -> playAudio(audioFile.getAbsolutePath()));
        tempPanel.add(btnPlay);

        JButton btnSend = new JButton("Gửi");
        btnSend.setBounds(200, 50, 80, 30);
        btnSend.addActionListener(e -> {
            controller.sendVoiceMessage(audioFile);
            tempVoiceDialog.dispose();
        });
        tempPanel.add(btnSend);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBounds(300, 50, 80, 30);
        btnCancel.addActionListener(e -> {
            if (audioFile.exists()) audioFile.delete();
            tempVoiceDialog.dispose();
        });
        tempPanel.add(btnCancel);

        tempVoiceDialog.getContentPane().add(tempPanel);
        tempVoiceDialog.setVisible(true);
    }

    private Object getClientController() {
        return controller;
    }
}