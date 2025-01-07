package hnu.chat.server.ui;

import hnu.chat.common.Constants;
import hnu.chat.common.Message;
import hnu.chat.server.network.ServerConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerFrame extends JFrame {
    private JPanel contentPane;
    private JTextArea logArea;
    private ServerConnection serverConnection;
    private SimpleDateFormat dateFormat;

    public ServerFrame() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        initializeFrame();
        initializeComponents();
        startServer();
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Chat Server");
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
    }

    private void initializeComponents() {
        // 初始化日志区域
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(247, 247, 247));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void startServer() {
        try {
            serverConnection = new ServerConnection();
            serverConnection.setMessageListener(this::logMessage);
            serverConnection.start();
            
            logMessage(new Message("Server", "服务器已启动", Message.MessageType.SYSTEM));
        } catch (Exception e) {
            logMessage(new Message("Server", "服务器启动失败: " + e.getMessage(), 
                Message.MessageType.SYSTEM));
            e.printStackTrace();
        }
    }

    public void logMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = dateFormat.format(new Date());
            String logMessage;
            
            if (message.isSystemMessage()) {
                logMessage = String.format("[%s] %s\n", timestamp, message.getContent());
            } else if (message.isImageMessage()) {
                logMessage = String.format("[%s] %s 发送了一张图片: %s\n", 
                    timestamp, message.getSender(), message.getFileName());
            } else if (message.getType() == Message.MessageType.FILE) {
                logMessage = String.format("[%s] %s 发送了一个文件: %s\n", 
                    timestamp, message.getSender(), message.getFileName());
            } else if (message.getType() == Message.MessageType.AUDIO) {
                logMessage = String.format("[%s] %s 发送了一个音频: %s\n", 
                    timestamp, message.getSender(), message.getFileName());
            } else if (message.getType() == Message.MessageType.VIDEO) {
                logMessage = String.format("[%s] %s 发送了一个视频: %s\n", 
                    timestamp, message.getSender(), message.getFileName());
            } else {
                logMessage = String.format("[%s] %s: %s\n", 
                    timestamp, message.getSender(), message.getContent());
            }
            
            logArea.append(logMessage);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ServerFrame frame = new ServerFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 