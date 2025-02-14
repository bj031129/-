package com.example.server.server;

import com.example.server.dto.SmtpSession;
import com.example.server.service.SmtpService;
import com.example.server.service.impl.SmtpServiceImpl;
import com.example.server.util.command.CommandConstant;
import com.example.server.util.command.CommandParse;
import com.example.server.util.json.SmtpStateCode;

import java.io.*;
import java.net.Socket;

import static com.example.server.util.command.CommandConstant.*;

/**
 * @author 曾佳宝
 */
public class SmtpServerRunnable implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    /**
     * 具体的SMTP指令的处理函数实现
     */
    private SmtpService smtpService;

    public SmtpServerRunnable(Socket socket) {
        this.socket = socket;
        smtpService = new SmtpServiceImpl(socket, new SmtpSession());
        System.out.println("开启 SMTP 服务线程");
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String command = reader.readLine();
                System.out.println("客户端: " + command);
                //解析客户端指令
                String[] args = CommandParse.parseCommand(command);
                if (args == null) {
                    writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
                } else {
                    if (HELO.equals(args[0])) {
                        System.out.println("执行" + HELO);
                        smtpService.handleHelloCommand(args);
                    } else if (CommandConstant.AUTH_LOGIN_PREFIX.equals(args[0])) {
                        System.out.println("执行" + AUTH_LOGIN);
                        smtpService.handleAuthCommand(args);
                    } else if (CommandConstant.MAIL_FROM_PREFIX.equals(args[0])) {
                        System.out.println("执行" + MAIL_FROM);
                        smtpService.handleMailCommand(args);
                    } else if (CommandConstant.RCPT_TO_PREFIX.equals(args[0])) {
                        smtpService.handleRcptCommand(args);
                        System.out.println("执行" + RCPT_TO);
                    } else if (CommandConstant.DATA.equals(args[0])) {
                        smtpService.handleDataCommand(args);
                        System.out.println("执行完成");
                    } else if (CommandConstant.REST.equals(args[0])) {
                        smtpService.handleResetCommand(args);
                    } else if (CommandConstant.QUIT.equals(args[0])) {
                        smtpService.handleQuitCommand(args);
                        reader.close();
                        writer.close();
                        socket.close();
                        break;
                    } else {
                        System.out.println("SMTP 没有命令" + args[0]);
                        writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(socket.getInetAddress() + " 断开连接");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            this.writer.println(SmtpStateCode.OPERATION_FAILED_DESC);
        }
    }
}
