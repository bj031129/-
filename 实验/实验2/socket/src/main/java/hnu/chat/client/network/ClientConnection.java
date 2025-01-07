package hnu.chat.client.network;

import hnu.chat.common.Constants;
import hnu.chat.common.Message;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientConnection {
    private String username;
    private String serverIp;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread listenerThread;
    private Consumer<Message> messageListener;
    private volatile boolean running = true;

    public ClientConnection(String username, String serverIp) {
        this.username = username;
        this.serverIp = serverIp;
    }

    public void connect() throws IOException {
        // 建立连接
        socket = new Socket(serverIp, Constants.SERVER_PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // 发送进入聊天室的消息
        Message joinMessage = new Message(username, "已进入聊天室", Message.MessageType.SYSTEM);
        sendMessage(joinMessage);

        // 启动消息监听线程
        startListening();
    }

    public void disconnect() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                Message leaveMessage = new Message(username, "已离开聊天室", Message.MessageType.SYSTEM);
                sendMessage(leaveMessage);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        if (socket == null || socket.isClosed()) {
            return;
        }

        try {
            String messageStr = formatMessage(message);
            out.println(messageStr);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            return message.getSender() + message.getContent();
        } else {
            return message.getSender() + ":" + message.getContent();
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
        } else {
            String[] parts = messageStr.split(":", 2);
            message = new Message(parts[0], parts[1], Message.MessageType.TEXT);
        }
        return message;
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    String messageStr = in.readLine();
                    if (messageStr == null) {
                        break;
                    }
                    
                    Message message = parseMessage(messageStr);
                    if (messageListener != null) {
                        messageListener.accept(message);
                    }
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        });
        listenerThread.start();
    }

    public void setMessageListener(Consumer<Message> listener) {
        this.messageListener = listener;
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
} 