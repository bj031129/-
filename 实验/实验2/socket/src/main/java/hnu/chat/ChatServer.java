package hnu.chat;

import hnu.chat.server.ui.ServerFrame;

public class ChatServer {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                ServerFrame frame = new ServerFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 