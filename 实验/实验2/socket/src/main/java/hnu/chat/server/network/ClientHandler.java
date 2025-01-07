package hnu.chat.server.network;

import hnu.chat.common.Constants;
import hnu.chat.common.Message;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ServerConnection server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private volatile boolean running = true;
    private Thread readThread;

    public ClientHandler(Socket socket, ServerConnection server) {
        this.socket = socket;
        this.server = server;
    }

    public void start() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // 读取客户端的第一条消息作为用户名
            String firstMessage = in.readLine();
            username = firstMessage.split("已")[0];
            
            // 注册客户端
            server.registerClient(username, this);
            
            // 启动消息读取线程
            readThread = new Thread(this::readMessages);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            if (username != null) {
                server.unregisterClient(username);
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        while (running) {
            try {
                String messageStr = in.readLine();
                if (messageStr == null) {
                    break;
                }
                
                Message message = parseMessage(messageStr);
                if (messageStr.contains("(") && messageStr.contains(")")) {
                    // 私聊消息
                    String recipient = messageStr.substring(
                        messageStr.indexOf("(") + 1,
                        messageStr.indexOf(")")
                    );
                    server.sendPrivateMessage(message, recipient);
                } else {
                    // 群发消息
                    server.broadcastMessage(message);
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
                break;
            }
        }
        stop();
    }

    public void sendMessage(Message message) {
        try {
            String messageStr = formatMessage(message);
            out.println(messageStr);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    private Message parseMessage(String messageStr) {
        Message message;
        if (messageStr.contains("已进入聊天室") || messageStr.contains("已离开聊天室")) {
            String sender = messageStr.split("已")[0];
            message = new Message(sender, messageStr, Message.MessageType.SYSTEM);
        } else if (messageStr.contains(Constants.IMG_START_TAG)) {
            String sender = messageStr.split(":")[0];
            String content = messageStr.substring(
                messageStr.indexOf(Constants.IMG_START_TAG) + Constants.IMG_START_TAG.length(),
                messageStr.indexOf(Constants.IMG_END_TAG)
            );
            String fileName = messageStr.substring(
                messageStr.indexOf(Constants.NAME_START_TAG) + Constants.NAME_START_TAG.length(),
                messageStr.indexOf(Constants.NAME_END_TAG)
            );
            message = new Message(sender, content, Message.MessageType.IMAGE);
            message.setFileName(fileName);
        } else if (messageStr.contains(Constants.FILE_START_TAG)) {
            String sender = messageStr.split(":")[0];
            String content = messageStr.substring(
                messageStr.indexOf(Constants.FILE_START_TAG) + Constants.FILE_START_TAG.length(),
                messageStr.indexOf(Constants.FILE_END_TAG)
            );
            String fileName = messageStr.substring(
                messageStr.indexOf(Constants.NAME_START_TAG) + Constants.NAME_START_TAG.length(),
                messageStr.indexOf(Constants.NAME_END_TAG)
            );
            message = new Message(sender, content, Message.MessageType.FILE);
            message.setFileName(fileName);
        } else if (messageStr.contains(Constants.AUDIO_START_TAG)) {
            String sender = messageStr.split(":")[0];
            String content = messageStr.substring(
                messageStr.indexOf(Constants.AUDIO_START_TAG) + Constants.AUDIO_START_TAG.length(),
                messageStr.indexOf(Constants.AUDIO_END_TAG)
            );
            String fileName = messageStr.substring(
                messageStr.indexOf(Constants.NAME_START_TAG) + Constants.NAME_START_TAG.length(),
                messageStr.indexOf(Constants.NAME_END_TAG)
            );
            message = new Message(sender, content, Message.MessageType.AUDIO);
            message.setFileName(fileName);
        } else if (messageStr.contains(Constants.VIDEO_START_TAG)) {
            String sender = messageStr.split(":")[0];
            String content = messageStr.substring(
                messageStr.indexOf(Constants.VIDEO_START_TAG) + Constants.VIDEO_START_TAG.length(),
                messageStr.indexOf(Constants.VIDEO_END_TAG)
            );
            String fileName = messageStr.substring(
                messageStr.indexOf(Constants.NAME_START_TAG) + Constants.NAME_START_TAG.length(),
                messageStr.indexOf(Constants.NAME_END_TAG)
            );
            message = new Message(sender, content, Message.MessageType.VIDEO);
            message.setFileName(fileName);
        } else {
            String[] parts = messageStr.split(":", 2);
            message = new Message(parts[0], parts[1], Message.MessageType.TEXT);
        }
        return message;
    }

    private String formatMessage(Message message) {
        if (message.isImageMessage()) {
            return String.format("%s:%s%s%s%s%s%s", message.getSender(), 
                Constants.IMG_START_TAG, message.getContent(), Constants.IMG_END_TAG,
                Constants.NAME_START_TAG, message.getFileName(), Constants.NAME_END_TAG);
        } else if (message.getType() == Message.MessageType.FILE) {
            return String.format("%s:%s%s%s%s%s%s", 
                message.getSender(),
                Constants.FILE_START_TAG, message.getContent(), Constants.FILE_END_TAG,
                Constants.NAME_START_TAG, message.getFileName(), Constants.NAME_END_TAG);
        } else if (message.getType() == Message.MessageType.AUDIO) {
            return String.format("%s:%s%s%s%s%s%s", 
                message.getSender(),
                Constants.AUDIO_START_TAG, message.getContent(), Constants.AUDIO_END_TAG,
                Constants.NAME_START_TAG, message.getFileName(), Constants.NAME_END_TAG);
        } else if (message.getType() == Message.MessageType.VIDEO) {
            return String.format("%s:%s%s%s%s%s%s", 
                message.getSender(),
                Constants.VIDEO_START_TAG, message.getContent(), Constants.VIDEO_END_TAG,
                Constants.NAME_START_TAG, message.getFileName(), Constants.NAME_END_TAG);
        } else if (message.isSystemMessage()) {
            return message.getContent();
        } else {
            return message.getSender() + ":" + message.getContent();
        }
    }
} 