package com.example.server.service.impl;

import com.example.server.config.SpringContextConfig;
import com.example.server.dto.SmtpSession;
import com.example.server.entity.Email;
import com.example.server.entity.ServerMessage;
import com.example.server.entity.User;
import com.example.server.mapper.AdminMapper;
import com.example.server.mapper.MailMapper;
import com.example.server.service.AuthService;
import com.example.server.service.ServerService;
import com.example.server.service.SmtpService;
import com.example.server.util.DateUtil;
import com.example.server.util.base64.Base64Util;
import com.example.server.util.command.CommandConstant;
import com.example.server.util.idGenerator.IdGenerator;
import com.example.server.util.json.SmtpStateCode;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * SMTP指令处理函数的具体实现
 *
 * @author 曾佳宝
 */
public class SmtpServiceImpl extends SmtpService {

    private final AuthService authService = SpringContextConfig.getBean("AuthServiceImpl");
    private final MailMapper mailMapper = SpringContextConfig.getBean(MailMapper.class);
    private final ServerService serverService = SpringContextConfig.getBean("ServerServiceImpl");

    /**
     * @param socket      当前连接的套接字
     * @param smtpSession 会话对象，用以保存用户的一些状态
     */
    public SmtpServiceImpl(Socket socket, SmtpSession smtpSession) {
        super(socket, smtpSession);
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleHelloCommand(String[] args) throws Exception {
        if (args.length != 2) {
            this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
            return;
        }
        this.writer.println(SmtpStateCode.SUCCESS_DESC);
        this.session.setHelloSent(true);
    }

    @Override
    public void handleAuthCommand(String[] args) throws Exception {
        if (!this.session.isHelloSent()) {
            this.writer.println(SmtpStateCode.SEQUENCE_ERROR + " send HELO first");
            return;
        }
        if (args.length != 2) {
            this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
        } else {
            String command = args[0] + args[1].toUpperCase();
            if (!CommandConstant.AUTH_LOGIN.replaceAll(" ", "").equals(command)) {
                this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
            } else {
                this.writer.println(SmtpStateCode.USERNAME_SENT_DESC);
                // base64 user
                String encodedUsername = this.reader.readLine();
                this.writer.println(SmtpStateCode.PASSWORD_SENT_DESC);
                // base64 pass
                String encodedPassword = this.reader.readLine();

                String username = Base64Util.decodeByBase64(encodedUsername.getBytes());
                String password = Base64Util.decodeByBase64(encodedPassword.getBytes());
                if (!serverService.isRightServer(username)) {
                    this.writer.println(SmtpStateCode.ADDRESS_NOT_AVAILABLE_DESC + "<\"" + username + "\">");
                    return;
                }
                username = username.split("@")[0];
                // 登录验证
                String result = authService.handleLogin(username, password);
                if ("SUCCESS".equals(result)) {
                    result = SmtpStateCode.AUTH_SUCCESS_DESC;
                    this.session.setAuthSent(true);
                    //这才是真正的发信人地址
                    this.session.setSender(username);
                    // 感觉这里还要把其他状态初始化成 false，因为它重新登录了
                } else {
                    result = SmtpStateCode.AUTH_FAILED_DESC;
                    // 重新登录但是验证失败
                    this.session.setAuthSent(false);
                }
                this.writer.println(result);
            }
        }
    }

    @Override
    public void handleMailCommand(String[] args) throws Exception { // 设置了发送方
        if (!this.session.isAuthSent()) {
            this.writer.println(SmtpStateCode.SEQUENCE_ERROR_DESC);
            return;
        }
        if (args.length < 3) {
            this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
        } else {
            int beginIndex = args[2].indexOf("<");
            int endIndex = args[2].indexOf(">");
            if (endIndex == -1 || beginIndex == -1 || args[2].length() <= 2) {
                this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
                return;
            }
            String username = args[2].substring(beginIndex + 1, endIndex);
            if (!serverService.isRightServer(username)) {
                this.writer.println(SmtpStateCode.ADDRESS_NOT_AVAILABLE_DESC + "<\"" + username + "\">");
                return;
            }
            username = username.split("@")[0];
            System.out.println("信封上的发件人: " + username);
            this.writer.println(SmtpStateCode.SUCCESS_DESC);
            this.session.setMailSent(true);
        }
    }

    // 设置了接收方
    @Override
    public void handleRcptCommand(String[] args) throws Exception {
        if (!this.session.isHelloSent() || !this.session.isAuthSent()) {
            this.writer.println(SmtpStateCode.SEQUENCE_ERROR_DESC);
        } else if (!this.session.isMailSent()) {
            this.writer.println("send MAIL FROM:<sender address> first");
        } else {
            if (args.length < 3) {
                this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
            } else {
                int beginIndex = args[2].indexOf("<");
                int endIndex = args[2].indexOf(">");
                if (endIndex == -1 || beginIndex == -1 || args[2].length() <= 2) {
                    this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
                    return;
                }
                String receiver = args[2].substring(beginIndex + 1, endIndex);
                System.out.println("收件人: " + receiver);
                if (!serverService.isRightServer(receiver)) {
                    this.writer.println(SmtpStateCode.ADDRESS_NOT_AVAILABLE_DESC + "<\"" + receiver + "\">");
                    return;
                }
                receiver = receiver.split("@")[0];
                this.session.getReceivers().add(receiver);
                User user = authService.findUserByUsername(receiver);
                if (user == null) {
                    this.writer.println(SmtpStateCode.ADDRESS_NOT_AVAILABLE_DESC + "<\"" + receiver + "\">");
                    return;
                }
                this.session.setRcptSent(true);
                this.writer.println(SmtpStateCode.SUCCESS_DESC);
            }
        }

    }

    @Override
    public void handleDataCommand(String[] args) throws Exception {
        if (!this.session.isHelloSent()) {
            this.writer.println(SmtpStateCode.SEQUENCE_ERROR + " send HELO first");
            return;
        } else if (!this.session.isMailSent() || !this.session.isRcptSent()) {
            this.writer.println(SmtpStateCode.SEQUENCE_ERROR_DESC);
            return;
        } else if (args.length > 2 || !CommandConstant.DATA.equals(args[0])) {
            this.writer.println(SmtpStateCode.COMMAND_ERROR_DESC);
            return;
        } else {
            this.writer.println(SmtpStateCode.START_EMAIL_INPUT_DESC);
        }
        String line = null;
        String from = null;
        String to = null;
        String subject = null;
        StringBuilder body = new StringBuilder();

        try {
            while (true) {
                line = this.reader.readLine();
                System.out.println(line);
                if (".".equals(line)) {
                    break;
                } else if (line.startsWith("from")) {
                    int index = line.indexOf(":");
                    from = line.substring(index + 1).trim();
                } else if (line.startsWith("to")) {
                    int index = line.indexOf(":");
                    to = line.substring(index + 1).trim();
                } else if (line.startsWith("subject")) {
                    int index = line.indexOf(":");
                    subject = line.substring(index + 1).trim();
                } else {
                    body.append(line);
                }
            }
            Integer sendedMail = mailMapper.getMailCount(this.session.getSender());
            User user = authService.findUserByUsername(this.session.getSender());
            if (user.getMailBoxSize() <= sendedMail) {
                this.writer.println(SmtpStateCode.MAILBOX_IS_FULL_DESC);
                System.out.println("邮箱已满");
                return;
            }
            for (String receiver : this.session.getReceivers()
            ) {
                Email email = new Email();
                Integer mid = IdGenerator.getId();
                email.setMid(mid);
                email.setSenderEmail(this.session.getSender());
                email.setReceiverEmail(receiver);
                email.setSubject(subject);
                email.setBody(body.toString());
                email.setSize(body.toString().getBytes().length);
                email.setSendTime(DateUtil.getCurrentTime());
                email.setSend(true);
                email.setRead(false);
                email.setDeleted(false);
                email.setTag(false);
                try {
                    System.out.println(email);
                    Integer rows = mailMapper.addMail(email);
                    if (rows != 1) {
                        this.writer.println("邮件发送失败,收件人为: " + receiver);
                        System.out.println("邮件发送失败,收件人为: " + receiver);
                    }
                } catch (Exception e) {
                    this.writer.println("邮件发送失败,收件人为: " + receiver);
                    System.out.println("邮件发送失败,收件人为: " + receiver);
                    e.printStackTrace();
                }
            }
            this.writer.println(SmtpStateCode.SUCCESS + " Send email Successful");
            System.out.println("发送成功");
            //清空发送人列表
            this.session.getReceivers().clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleResetCommand(String[] args) throws Exception {
        this.writer.println(SmtpStateCode.SUCCESS_DESC);
    }

    @Override
    public void handleQuitCommand(String[] args) throws Exception {
        try {
            this.writer.println(SmtpStateCode.BYE);
            //关闭连接
            this.socket.close();
            //初始化会话
            this.session = new SmtpSession();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
