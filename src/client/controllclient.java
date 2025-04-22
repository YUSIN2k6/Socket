package client;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.nio.file.Files;

public class controllclient {
    private clientUI view;
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private Thread receiveThread;

    public controllclient(clientUI view) {
        this.view = view;
        initializeClient();
        setupEventListeners();
    }

    private void initializeClient() {
        try {
            socket = new Socket("localhost", 1234);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            System.out.println("Connected to server");

            output.writeByte(0);
            output.writeUTF("USERNAME:" + view.getUsername());
            output.flush();

            receiveThread = new Thread(this::receiveMessages);
            receiveThread.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Không thể kết nối tới server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupEventListeners() {
        view.getLbsent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sendMessage();
            }
        });

        view.getLbfile().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                File selectedFile = view.chooseFile();
                if (selectedFile != null) {
                    try {
                        String fileName = selectedFile.getName();
                        String filePath = selectedFile.getAbsolutePath();
                        byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                        output.writeByte(1);
                        output.writeUTF(fileName);
                        output.writeLong(fileContent.length);
                        output.write(fileContent);
                        output.flush();
                        JPanel filePanel = view.createFilePanel(fileName, filePath);
                        view.showMessagePanel(filePanel);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(view, "Lỗi khi gửi file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        view.getLbimg().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                File selectedImage = view.chooseImage();
                if (selectedImage != null) {
                    try {
                        String fileName = selectedImage.getName();
                        String filePath = selectedImage.getAbsolutePath();
                        byte[] fileContent = Files.readAllBytes(selectedImage.toPath());
                        output.writeByte(2);
                        output.writeUTF(fileName);
                        output.writeLong(fileContent.length);
                        output.write(fileContent);
                        output.flush();
                        JPanel imgPanel = view.createImgPanel(filePath, null);
                        view.showMessagePanel(imgPanel);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(view, "Lỗi khi gửi ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        view.getLbemoji().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                view.showEmojiPanel();
            }
        });

        view.getLbimgsecret().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                File selectedImage = view.chooseImage();
                if (selectedImage != null) {
                    String secretMessage = view.showSecretMessageInput();
                    if (secretMessage != null) {
                        try {
                            String fileName = selectedImage.getName();
                            String filePath = selectedImage.getAbsolutePath();
                            byte[] fileContent = Files.readAllBytes(selectedImage.toPath());
                            output.writeByte(3); // Mã cho ảnh bí mật
                            output.writeUTF(fileName);
                            output.writeLong(fileContent.length);
                            output.write(fileContent);
                            output.writeUTF(secretMessage);
                            output.flush();
                            JPanel imgPanel = view.createImgPanel(filePath, secretMessage);
                            view.showMessagePanel(imgPanel);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(view, "Lỗi khi gửi ảnh bí mật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        view.getLbvoice().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                view.startRecording();
            }
        });
    }

    public void sendVoiceMessage(File audioFile) {
        try {
            String fileName = audioFile.getName();
            String filePath = audioFile.getAbsolutePath();
            byte[] fileContent = Files.readAllBytes(audioFile.toPath());
            output.writeByte(5); // Mã cho tin nhắn thoại
            output.writeUTF(fileName);
            output.writeLong(fileContent.length);
            output.write(fileContent);
            output.flush();
            JPanel voicePanel = view.createVoicePanel(filePath);
            view.showMessagePanel(voicePanel);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi gửi tin nhắn thoại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String message = view.getChattextarea().getText().trim();

        if (!message.isEmpty()) { // Chỉ kiểm tra message không rỗng
            try {
                boolean isEncrypted = view.getCboxmahoa().isSelected();
                String finalMessage = message;
                if (isEncrypted) {
                    finalMessage = view.encryptMessage(message);
                    output.writeByte(4); // Mã cho tin nhắn mã hóa
                } else {
                    output.writeByte(0); // Mã cho tin nhắn text
                }
                output.writeUTF(finalMessage);
                output.flush();
                JPanel mePanel = view.createMePanel(view.getUsername() + ": " + finalMessage);
                view.showMessagePanel(mePanel);
                view.getChattextarea().setText(""); // Đặt lại thành chuỗi rỗng
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi gửi tin nhắn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void receiveMessages() {
        try {
            while (true) {
                byte messageType = input.readByte();
                if (messageType == 0) { // Tin nhắn text
                    String message = input.readUTF();
                    SwingUtilities.invokeLater(() -> {
                        if (!message.startsWith(view.getUsername() + ": ")) {
                            JPanel youPanel = view.createYouPanel(message, false);
                            view.showMessagePanel(youPanel);
                        }
                    });
                } else if (messageType == 1) { // File
                    String fileName = input.readUTF();
                    long fileSize = input.readLong();
                    byte[] fileContent = new byte[(int) fileSize];
                    input.readFully(fileContent);
                    File tempFile = view.saveTempFile(fileName, fileContent);
                    if (tempFile != null) {
                        SwingUtilities.invokeLater(() -> {
                            JPanel filePanel = view.createFilePanel(fileName, tempFile.getAbsolutePath());
                            view.showMessagePanel(filePanel);
                        });
                    }
                } else if (messageType == 2) { // Ảnh
                    String fileName = input.readUTF();
                    long fileSize = input.readLong();
                    byte[] fileContent = new byte[(int) fileSize];
                    input.readFully(fileContent);
                    File tempFile = view.saveTempFile(fileName, fileContent);
                    if (tempFile != null) {
                        SwingUtilities.invokeLater(() -> {
                            JPanel imgPanel = view.createImgPanel(tempFile.getAbsolutePath(), null);
                            view.showMessagePanel(imgPanel);
                        });
                    }
                } else if (messageType == 3) { // Ảnh bí mật
                    String fileName = input.readUTF();
                    long fileSize = input.readLong();
                    byte[] fileContent = new byte[(int) fileSize];
                    input.readFully(fileContent);
                    String secretMessage = input.readUTF();
                    File tempFile = view.saveTempFile(fileName, fileContent);
                    if (tempFile != null) {
                        String finalSecretMessage = secretMessage;
                        SwingUtilities.invokeLater(() -> {
                            JPanel imgPanel = view.createImgPanel(tempFile.getAbsolutePath(), finalSecretMessage);
                            view.showMessagePanel(imgPanel);
                        });
                    }
                } else if (messageType == 4) { // Tin nhắn mã hóa
                    String message = input.readUTF();
                    SwingUtilities.invokeLater(() -> {
                        if (!message.startsWith(view.getUsername() + ": ")) {
                            JPanel youPanel = view.createYouPanel(message, true);
                            view.showMessagePanel(youPanel);
                        }
                    });
                } else if (messageType == 5) { // Tin nhắn thoại
                    String fileName = input.readUTF();
                    long fileSize = input.readLong();
                    byte[] fileContent = new byte[(int) fileSize];
                    input.readFully(fileContent);
                    File tempFile = view.saveTempFile(fileName, fileContent);
                    if (tempFile != null) {
                        SwingUtilities.invokeLater(() -> {
                            JPanel voicePanel = view.createVoicePanel(tempFile.getAbsolutePath());
                            view.showMessagePanel(voicePanel);
                        });
                    }
                }
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(view, "Mất kết nối với server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE)
            );
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}