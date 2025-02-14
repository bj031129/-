package com.example.server.service.impl;

import com.example.server.config.SpringContextConfig;
import com.example.server.dto.Pop3Session;
import com.example.server.entity.Email;
import com.example.server.mapper.AdminMapper;
import com.example.server.mapper.MailMapper;
import com.example.server.service.AuthService;
import com.example.server.service.Pop3Service;
import com.example.server.service.ServerService;
import com.example.server.util.json.Pop3StateCode;

import com.example.server.util.json.SmtpStateCode;
import java.io.*;
import java.net.Socket;

public class Pop3ServiceImpl extends Pop3Service {

    private final AuthService authService = SpringContextConfig.getBean("AuthServiceImpl");
    private final MailMapper mailMapper = SpringContextConfig.getBean(MailMapper.class);
    private final ServerService serverService = SpringContextConfig.getBean("ServerServiceImpl");

    public Pop3ServiceImpl(Socket socket, Pop3Session pop3Session) {
        super(socket, pop3Session);
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收用户名
     *
     * @param args 参数列表
     */
    @Override
    public void handleUserCommand(String[] args) {
        if (args.length != 2) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.VALID + '\n' + "#end#");
        }
        try {
            if (!serverService.isRightServer(args[1])) {
                this.writer.println(
                    SmtpStateCode.ADDRESS_NOT_AVAILABLE_DESC + "<\"" + args[1] + "\">"+'\n' + "#end#");
                return;
            }
            String username = args[1].split("@")[0];
            pop3Session.setUserSent(true);
            pop3Session.setAuth(false);
            pop3Session.setUsername(username);
            this.writer.println(Pop3StateCode.OK + '\n' + "#end#");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收用户密码 → 登录验证 → 加载邮件
     *
     * @param args 参数列表
     */
    @Override
    public void handlePassCommand(String[] args) {
        if (args.length != 2) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.VALID + '\n' + "#end#");
            return;
        } else if (!pop3Session.isUserSent()) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.USER + '\n' + "#end#");
            return;
        }
        String username = pop3Session.getUsername();
        String password = args[1];
        String result = authService.handleLogin(username, password);
        if ("SUCCESS".equals(result)) {
            // 加载邮件
            pop3Session.setEmails(mailMapper.findMailsByRcpt(username));
            result = Pop3StateCode.OK + pop3Session.getCount() + " " + Pop3StateCode.AUTH_SUCCESS;
            pop3Session.setAuth(true);
        } else if("USER_IS_LOG_OUT".equals(result)){
            result = Pop3StateCode.ERR +Pop3StateCode.AUTH_LOG_OUT;
            pop3Session.setAuth(true);
        }

        else {
            result = Pop3StateCode.ERR + Pop3StateCode.AUTH_FAIL;
            // 重新登录但是验证失败
            pop3Session.setAuth(false);
        }
        this.writer.println(result + '\n' + "#end#");
    }

    /**
     * 统计邮件信息，返回 邮件总数 和 总字节数
     *
     * @param args 参数列表
     */
    @Override
    public void handleStatCommand(String[] args) {
        if (!pop3Session.isAuth()) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.AUTH_FAIL + '\n' + "#end#");
            return;
        }
        pop3Session.setEmails(mailMapper.findMailsByRcpt(pop3Session.getUsername()));
        this.writer.println(Pop3StateCode.OK + pop3Session.getCount() + ' ' + pop3Session.getTotalSize() + '\n' + "#end#");
    }

    /**
     * 列出邮件信息，每个信息有两列：序号(从1开始) 字节大小
     *
     * @param args 参数列表
     */
    @Override
    public void handleListCommand(String[] args) {
        if (!pop3Session.isAuth()) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.AUTH_FAIL + '\n' + "#end#");
            return;
        }
        String result = Pop3StateCode.OK;
        pop3Session.setEmails(mailMapper.findMailsByRcpt(pop3Session.getUsername()));
        System.out.println(pop3Session.getEmails());
        result = Pop3StateCode.OK + pop3Session.getCount() + " " + Pop3StateCode.AUTH_SUCCESS;
        pop3Session.setAuth(true);
        int index = 1;
        for (Email email : pop3Session.getEmails()) {
            result += "" + '\n' + index + ' ' + email.getSize();
            ++index;
        }
        this.writer.println(result + '\n' + "#end#");
    }

    /**
     * 如 RETR 1，表示取出编号为 i 的邮件
     *
     * @param args 参数列表
     */
    @Override
    public void handleRetrCommand(String[] args) {
        if (args.length != 2) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.VALID + '\n' + "#end#");
            return;
        } else if (!pop3Session.isAuth()) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.AUTH_FAIL + '\n' + "#end#");
            return;
        }
        // 取回的邮件编号 大于 邮件数量
        int index = Integer.parseInt(args[1]) - 1;
        if (index > pop3Session.getCount() - 1 || index < 0) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.OUT_OF_INDEX + '\n' + "#end#");
            return;
        }
        try {
            String serverName = serverService.getServerName();
            String result = Pop3StateCode.OK;
            this.writer.println(result);
            result = "";
            Email email = pop3Session.getEmails().get(index);
            System.out.println(email);
            result += "From: <" + email.getSenderEmail() + serverName + ">" + '\n';
            result += "To: <" + email.getReceiverEmail() + serverName + ">" + '\n';
            result += "SendTime: <" + email.getSendTime() + ">" + '\n';
            result += "Subject: <" + email.getSubject() + ">" + '\n';
            result += "Body: <";
            result += email.getBody() + ">" + '\n';
            this.writer.println(result + "#end#");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 标记想删除哪些邮件
     * 如：DELE 1，表示将邮件 1 的 delete 设置为 true，但是在 QUIT 命令之后才会真正删除
     *
     * @param args 参数列表
     */
    @Override
    public void handleDeleCommand(String[] args) {
        if (args.length != 2) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.VALID + '\n' + "#end#");
            return;
        }
        if (!pop3Session.isAuth()) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.AUTH_FAIL + '\n' + "#end#");
            return;
        }
        // 标记的邮件编号 大于 邮件数量
        int index = Integer.parseInt(args[1]) - 1;
        if (index > pop3Session.getCount() - 1) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.OUT_OF_INDEX + '\n' + "#end#");
            return;
        }
        pop3Session.getEmails().get(index).setDeleted(true);
        this.writer.println(Pop3StateCode.OK + '\n' + "#end#");
    }

    /**
     * 取消标记删除
     * 如：REST 1，表示将邮件 1 的 delete 设置为 false
     *
     * @param args 参数列表
     */
    @Override
    public void handleRestCommand(String[] args) {
        if (args.length != 2) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.VALID + '\n' + "#end#");
            return;
        }
        if (!pop3Session.isAuth()) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.AUTH_FAIL + '\n' + "#end#");
            return;
        }
        // 取消标记的邮件编号 大于 邮件数量
        int index = Integer.parseInt(args[1]) - 1;
        if (index > pop3Session.getCount() - 1) {
            this.writer.println(Pop3StateCode.ERR + Pop3StateCode.OUT_OF_INDEX + '\n' + "#end#");
            return;
        }
        pop3Session.getEmails().get(index).setDeleted(false);
        this.writer.println(Pop3StateCode.OK + '\n' + "#end#");
    }

    /**
     * 断开服务器，此时要执行真正的删除
     *
     * @param args 指令参数
     */
    @Override
    public void handleQuitCommand(String[] args) {
        this.writer.println(Pop3StateCode.OK + Pop3StateCode.BYE + '\n' + "#end#");
        if (pop3Session.isAuth()) {
            for (Email email : pop3Session.getEmails()) {
                if (email.getDeleted()) {
                    mailMapper.deleteMailByMid(email.getMid());
                }
            }
        }
        try {
            //关闭连接
            this.socket.close();
            //初始化会话
            this.pop3Session = new Pop3Session();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
