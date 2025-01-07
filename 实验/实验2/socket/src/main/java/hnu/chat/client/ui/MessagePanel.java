package hnu.chat.client.ui;

import hnu.chat.common.Constants;
import hnu.chat.common.Message;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import javax.imageio.ImageIO;

public class MessagePanel extends JPanel {
    private JTextPane textPane;
    private SimpleAttributeSet rightAlign;
    private SimpleAttributeSet leftAlign;
    private String username;
    private SimpleDateFormat dateFormat;

    public MessagePanel(String username) {
        this.username = username;
        this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(new Color(247, 247, 247));
        
        rightAlign = new SimpleAttributeSet();
        StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
        
        leftAlign = new SimpleAttributeSet();
        StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(Message message) {
        StyledDocument doc = textPane.getStyledDocument();
        boolean isOwnMessage = message.getSender().equals(username);
        SimpleAttributeSet alignment = isOwnMessage ? rightAlign : leftAlign;

        try {
            // 插入时间
            doc.setParagraphAttributes(doc.getLength(), 1, alignment, false);
            doc.insertString(doc.getLength(), 
                dateFormat.format(message.getTimestamp()) + "\n", 
                alignment);

            // 根据消息类型处理内容
            if (message.isImageMessage()) {
                handleImageMessage(message, doc, alignment);
            } else if (message.getType() == Message.MessageType.FILE) {
                handleFileMessage(message, doc, alignment);
            } else if (message.getType() == Message.MessageType.AUDIO) {
                handleAudioMessage(message, doc, alignment);
            } else if (message.getType() == Message.MessageType.VIDEO) {
                handleVideoMessage(message, doc, alignment);
            } else {
                handleTextMessage(message, doc, alignment);
            }

            // 滚动到最新消息
            textPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleImageMessage(Message message, StyledDocument doc, SimpleAttributeSet alignment) 
            throws BadLocationException, IOException {
        // 插入发送者信息
        doc.insertString(doc.getLength(), 
            message.getSender() + " 发送了一张图片：\n", 
            alignment);

        // 创建新段落用于图片显示
        doc.insertString(doc.getLength(), "\n", alignment);
        doc.setParagraphAttributes(doc.getLength() - 1, 1, alignment, false);

        // 处理图片
        BufferedImage image = ImageHandler.base64ToImage(message.getContent());
        
        // 调整图片大小
        int width = image.getWidth();
        int height = image.getHeight();
        
        if (width > Constants.DISPLAY_IMAGE_WIDTH || height > Constants.DISPLAY_IMAGE_HEIGHT) {
            double scale = Math.min(
                (double)Constants.DISPLAY_IMAGE_WIDTH/width, 
                (double)Constants.DISPLAY_IMAGE_HEIGHT/height
            );
            width = (int)(width * scale);
            height = (int)(height * scale);
        }

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImage.createGraphics().drawImage(image, 0, 0, width, height, null);

        // 创建图片标签
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(
            alignment == rightAlign ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT
        );

        // 创建右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem saveItem = new JMenuItem("保存图片");
        saveItem.addActionListener(e -> saveImage(message));
        popupMenu.add(saveItem);

        // 添加鼠标监听器
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) { // 右键点击
                    popupMenu.show(imageLabel, e.getX(), e.getY());
                }
            }
        });

        // 插入图片
        textPane.setCaretPosition(doc.getLength() - 1);
        textPane.insertComponent(imageLabel);

        // 插入图片后的空行
        doc.insertString(doc.getLength(), "\n\n", alignment);
    }

    private void saveImage(Message message) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(message.getFileName()));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // 解码Base64图片数据
                BufferedImage image = ImageHandler.base64ToImage(message.getContent());
                
                // 获取文件扩展名
                String fileName = fileChooser.getSelectedFile().getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                
                // 如果文件名没有扩展名，默认使用PNG
                if (!fileName.contains(".")) {
                    extension = "png";
                    fileChooser.setSelectedFile(new File(fileChooser.getSelectedFile().getPath() + ".png"));
                }
                
                // 保存图片
                ImageIO.write(image, extension, fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "图片保存成功");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "图片保存失败: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void handleFileMessage(Message message, StyledDocument doc, SimpleAttributeSet alignment) 
            throws BadLocationException {
        // 创建文件保存按钮
        JButton saveButton = new JButton("保存文件: " + message.getFileName());
        saveButton.addActionListener(e -> saveFile(message));
        
        // 插入发送者信息
        doc.insertString(doc.getLength(), 
            message.getSender() + " 发送了一个文件：\n", 
            alignment);
        
        // 插入保存按钮
        textPane.setCaretPosition(doc.getLength());
        textPane.insertComponent(saveButton);
        doc.insertString(doc.getLength(), "\n\n", alignment);
    }

    private void saveFile(Message message) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(message.getFileName()));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] fileData = Base64.getDecoder().decode(message.getContent());
                java.nio.file.Files.write(fileChooser.getSelectedFile().toPath(), fileData);
                JOptionPane.showMessageDialog(this, "文件保存成功");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "文件保存失败");
                ex.printStackTrace();
            }
        }
    }

    private void handleAudioMessage(Message message, StyledDocument doc, SimpleAttributeSet alignment) 
            throws BadLocationException {
        JButton playButton = new JButton("播放音频: " + message.getFileName());
        JButton saveButton = new JButton("保存音频");
        
        playButton.addActionListener(e -> playMedia(message, true));
        saveButton.addActionListener(e -> saveMedia(message));
        
        // 插入发送者信息
        doc.insertString(doc.getLength(), 
            message.getSender() + " 发送了一个音频文件：\n", 
            alignment);
        
        // 插入按钮
        textPane.setCaretPosition(doc.getLength());
        textPane.insertComponent(playButton);
        textPane.insertComponent(saveButton);
        doc.insertString(doc.getLength(), "\n\n", alignment);
    }

    private void handleVideoMessage(Message message, StyledDocument doc, SimpleAttributeSet alignment) 
            throws BadLocationException {
        JButton playButton = new JButton("播放视频: " + message.getFileName());
        JButton saveButton = new JButton("保存视频");
        
        playButton.addActionListener(e -> playMedia(message, false));
        saveButton.addActionListener(e -> saveMedia(message));
        
        // 插入发送者信息
        doc.insertString(doc.getLength(), 
            message.getSender() + " 发送了一个视频文件：\n", 
            alignment);
        
        // 插入按钮
        textPane.setCaretPosition(doc.getLength());
        textPane.insertComponent(playButton);
        textPane.insertComponent(saveButton);
        doc.insertString(doc.getLength(), "\n\n", alignment);
    }

    private void playMedia(Message message, boolean isAudio) {
        try {
            // 创建临时文件
            String extension = message.getFileName().substring(
                message.getFileName().lastIndexOf('.')
            );
            File tempFile = File.createTempFile("media", extension);
            tempFile.deleteOnExit();
            
            byte[] mediaData = Base64.getDecoder().decode(message.getContent());
            java.nio.file.Files.write(tempFile.toPath(), mediaData);
            
            if (isAudio) {
                // 使用默认播放器播放音频
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(tempFile);
                } else {
                    // 如果系统不支持默认播放器，可以使用其他方式播放
                    ProcessBuilder pb = new ProcessBuilder();
                    
                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                        pb.command("cmd", "/c", "start", tempFile.getAbsolutePath());
                    } else {
                        pb.command("xdg-open", tempFile.getAbsolutePath());
                    }
                    pb.start();
                }
            } else {
                // 使用系统默认播放器播放视频
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(tempFile);
                    } else {
                        ProcessBuilder pb = new ProcessBuilder();
                        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                            pb.command("cmd", "/c", "start", tempFile.getAbsolutePath());
                        } else {
                            pb.command("xdg-open", tempFile.getAbsolutePath());
                        }
                        pb.start();
                    }
                } catch (Exception e) {
                    throw new Exception("无法打开默认视频播放器: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "播放失败: " + ex.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMedia(Message message) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(message.getFileName()));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] mediaData = Base64.getDecoder().decode(message.getContent());
                java.nio.file.Files.write(fileChooser.getSelectedFile().toPath(), mediaData);
                JOptionPane.showMessageDialog(this, "保存成功");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存失败");
                ex.printStackTrace();
            }
        }
    }

    private void handleTextMessage(Message message, StyledDocument doc, SimpleAttributeSet alignment) 
            throws BadLocationException {
        if (message.isSystemMessage()) {
            doc.insertString(doc.getLength(), message.getContent() + "\n\n", alignment);
        } else {
            doc.insertString(doc.getLength(), 
                message.getSender() + ": " + message.getContent() + "\n\n", 
                alignment);
        }
    }

    public void clear() {
        textPane.setText("");
    }
} 