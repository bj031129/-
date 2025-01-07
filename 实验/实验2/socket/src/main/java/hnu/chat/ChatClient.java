package hnu.chat;

import hnu.chat.client.ui.ClientFrame;

public class ChatClient {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                ClientFrame frame = new ClientFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 