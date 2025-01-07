package hnu.chat.server.network;

import hnu.chat.common.Constants;
import hnu.chat.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerConnection {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> clients;
    private Consumer<Message> messageListener;
    private volatile boolean running = true;
    private Thread acceptThread;

    public ServerConnection() throws IOException {
        serverSocket = new ServerSocket(Constants.SERVER_PORT);
        clients = new ConcurrentHashMap<>();
    }

    public void start() {
        acceptThread = new Thread(this::acceptClients);
        acceptThread.start();
    }

    private void acceptClients() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                handler.start();
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            clients.values().forEach(ClientHandler::stop);
            clients.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerClient(String username, ClientHandler handler) {
        clients.put(username, handler);
        broadcastSystemMessage(username + "已进入聊天室");
    }

    public void unregisterClient(String username) {
        clients.remove(username);
        broadcastSystemMessage(username + "已离开聊天室");
    }

    public void broadcastMessage(Message message) {
        String sender = message.getSender();
        clients.forEach((username, handler) -> {
            if (!username.equals(sender)) {
                handler.sendMessage(message);
            }
        });
        notifyMessageListener(message);
    }

    public void sendPrivateMessage(Message message, String recipient) {
        ClientHandler handler = clients.get(recipient);
        if (handler != null) {
            handler.sendMessage(message);
            notifyMessageListener(message);
        }
    }

    private void broadcastSystemMessage(String content) {
        Message message = new Message("Server", content, Message.MessageType.SYSTEM);
        clients.values().forEach(handler -> handler.sendMessage(message));
        notifyMessageListener(message);
    }

    private void notifyMessageListener(Message message) {
        if (messageListener != null) {
            messageListener.accept(message);
        }
    }

    public void setMessageListener(Consumer<Message> listener) {
        this.messageListener = listener;
    }
} 