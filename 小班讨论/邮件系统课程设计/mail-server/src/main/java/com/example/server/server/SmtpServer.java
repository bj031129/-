package com.example.server.server;

import com.example.server.config.SpringContextConfig;
import com.example.server.entity.ServerMessage;
import com.example.server.mapper.AdminMapper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 曾佳宝
 */
public class SmtpServer extends Thread {

    private static ServerSocket serverSocket;
    private static int port;
    private static boolean shutDown;
    private static ThreadPoolExecutor executor;
    private static List<Socket> clients;

    private int port1;

    public int getPort1() {
        return port1;
    }

    public void setPort1(int port1) {
        this.port1 = port1;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        SmtpServer.port = port;
    }

    public static boolean isShutDown() {
        return shutDown;
    }

    public static void setShutDown(boolean shutDown) {
        SmtpServer.shutDown = shutDown;
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static void setServerSocket(ServerSocket serverSocket) {
        SmtpServer.serverSocket = serverSocket;
    }

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static void setExecutor(ThreadPoolExecutor executor) {
        SmtpServer.executor = executor;
    }

    public static List<Socket> getClients() {
        return clients;
    }

    public static void setClients(List<Socket> clients) {
        SmtpServer.clients = clients;
    }

    /**
     * 默认端口号为25
     */
    public SmtpServer() {
        List<ServerMessage> serverMessage = null;
        try {
            AdminMapper adminMapper = SpringContextConfig.getBean(AdminMapper.class);
            serverMessage = adminMapper.selectServerMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serverMessage == null || serverMessage.size() == 0) {
            port = 25;
        } else {
            ServerMessage msg = serverMessage.get(0);
            port1 = msg.getSmtpPort();
            port = port1;
        }
        shutDown = false;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executor = new ThreadPoolExecutor(30, 50, 1000, unit, workQueue, threadFactory);
        clients = new LinkedList<>();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("SMTP 服务已开启");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopSmtpServer() throws IOException {
        for (Socket socket : clients
        ) {
            socket.close();
        }
        System.out.println("关闭SMTP服务器");
        SmtpServer.shutDown = true;
        serverSocket.close();
        executor.shutdown();
        this.interrupt();
    }

    /**
     * 循环等待客户端得请求连接
     * 建立连接后, 开启一个服务线程处理该连接
     */
    @Override
    public void run() {
        try {
            while (true) {
                if(isShutDown()){
                    return;
                }
                Socket socket = serverSocket.accept();
                clients.add(socket);
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                printWriter.println("220 SMTP ready");
                SmtpServerRunnable t = new SmtpServerRunnable(socket);
                executor.execute(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SmtpServer smtpServer = new SmtpServer();
        smtpServer.start();
    }

}
