package com.example.server.controller;

import com.example.server.config.SpringContextConfig;
import com.example.server.dto.*;
import com.example.server.entity.Email;
import com.example.server.entity.Filter;
import com.example.server.entity.Log;
import com.example.server.entity.ServerMessage;
import com.example.server.entity.User;
import com.example.server.server.Pop3Server;
import com.example.server.server.SmtpServer;
import com.example.server.service.AdminService;
import com.example.server.service.ServerService;
import com.example.server.service.impl.AdminServiceImpl;
import com.example.server.util.annotation.IsAdmin;
import com.example.server.util.json.JsonResult;
import com.example.server.util.json.JsonResultFactory;
import com.example.server.util.json.JsonResultStateCode;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 曾佳宝
 */
@Api
@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService = SpringContextConfig.getBean(AdminServiceImpl.class);
    private final ServerService serverService = SpringContextConfig.getBean("ServerServiceImpl");

    @PostMapping("/delete-users")
    @IsAdmin
    public JsonResult handleDeleteUser(@RequestBody List<String> usernames) {
        Integer rows = adminService.deleteUsersByUsername(usernames);
        if (rows != null) {
            if (rows.equals(usernames.size())) {
                return JsonResultFactory.buildSuccessResult();
            } else {
                return JsonResultFactory.buildJsonResult(JsonResultStateCode.OPERATION_IS_NOT_COMPLETED, JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC, null);
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/logout-users")
    @IsAdmin
    public JsonResult handleLogout(@RequestBody List<String> usernames) {
        Integer rows = adminService.updateUsersLogState(usernames, true);
        if (rows != null) {
            if (rows.equals(usernames.size())) {
                return JsonResultFactory.buildSuccessResult();
            } else {
                return JsonResultFactory.buildJsonResult(JsonResultStateCode.OPERATION_IS_NOT_COMPLETED, JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC, null);
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/login-users")
    @IsAdmin
    public JsonResult handleLogin(@RequestBody List<String> usernames) {
        List<String> afterUsername = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            afterUsername.add(usernames.get(i).split("@")[0]);
        }
        Integer rows = adminService.updateUsersLogState(afterUsername, false);
        if (rows != null) {
            if (rows.equals(usernames.size())) {
                return JsonResultFactory.buildSuccessResult();
            } else {
                return JsonResultFactory.buildJsonResult(JsonResultStateCode.OPERATION_IS_NOT_COMPLETED, JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC, null);
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @GetMapping("/get-users")
    @IsAdmin
    public JsonResult handleGetUsers() {
        List<User> userList = adminService.getAllUsers();
        serverService.addServerNameForUser(userList);
        System.out.println(userList);
        if (userList != null) {
            if (userList.size() > 0) {
                return JsonResultFactory.buildJsonResult(
                        JsonResultStateCode.SUCCESS,
                        JsonResultStateCode.SUCCESS_DESC,
                        userList
                );
            } else {
                return JsonResultFactory.buildJsonResult(
                        JsonResultStateCode.NOT_FOUND,
                        JsonResultStateCode.NOT_FOUND_DESC,
                        null
                );
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/auth")
    @IsAdmin
    public JsonResult handleAuthorize(@RequestBody List<UserNameAndType> userNameAndTypes) {

        if (userNameAndTypes != null) {
            List<String> usernames = new LinkedList<>();
            for (UserNameAndType user : userNameAndTypes
            ) {
                usernames.add(user.getUsername().split("@")[0]);
            }
            Integer type = userNameAndTypes.get(0).getAccountType();
            Integer rows = adminService.auth(usernames, type);
            if (rows != null) {
                if (rows.equals(userNameAndTypes.size())) {
                    return JsonResultFactory.buildSuccessResult();
                } else {
                    return JsonResultFactory.
                            buildJsonResult(
                                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED,
                                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC, null);
                }
            } else {
                return JsonResultFactory.buildFailureResult();
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }

    }

    @PostMapping("/create-user")
    @IsAdmin
    public JsonResult handleCreate(@RequestBody NewUserMessage userMessage) {

        Integer rows;
        rows = adminService.createUser(userMessage);
        if (rows != null) {
            if (rows == 1) {
                return JsonResultFactory.buildSuccessResult();
            } else {
                return JsonResultFactory.buildFailureResult();
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/change-server-state")
    @IsAdmin
    public JsonResult handleChangeServerState(@RequestBody ServerStateMsg serverState) {
        Integer state = adminService.changeServerState(serverState);
        JsonResult res = JsonResultFactory.buildSuccessResult();
        ServerPortMsg msg = new ServerPortMsg();
        if (state != null && state == 1) {
            if (serverState.getServerState()) {
                if (serverState.getServerType() == 0) {
                    msg.setServerPort(String.valueOf(SmtpServer.getPort()));
                    msg.setServerType(serverState.getServerType());
                    msg.setSid(serverState.getSid());
                } else if (serverState.getServerType() == 1) {
                    msg.setServerPort(String.valueOf(Pop3Server.getPort()));
                    msg.setServerType(serverState.getServerType());
                    msg.setSid(serverState.getSid());
                } else {
                    return JsonResultFactory.buildJsonResult(JsonResultStateCode.FAILED, "没有该服务", null);
                }
                adminService.restartServer(msg);
            } else {
                if (serverState.getServerType() == 0) {
                    msg.setServerPort(String.valueOf(SmtpServer.getPort()));
                    msg.setServerType(serverState.getServerType());
                    msg.setSid(serverState.getSid());

                } else if (serverState.getServerType() == 1) {
                    msg.setServerPort(String.valueOf(Pop3Server.getPort()));
                    msg.setServerType(serverState.getServerType());
                    msg.setSid(serverState.getSid());
                } else {
                    return JsonResultFactory.buildJsonResult(JsonResultStateCode.FAILED, "没有该服务", null);
                }
                adminService.stopServer(msg);
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }
        return res;
    }

    @PostMapping("/change-server-port")
    @IsAdmin
    public JsonResult handleChangeServerPort(@RequestBody ServerPortMsg msg) {

        //修改端口号
        Integer row = adminService.changeServerPort(msg);
        JsonResult res = JsonResultFactory.buildSuccessResult();
        JsonResult res1 = JsonResultFactory.buildFailureResult();
        if (row != null && row == 1) {
            //暂停当前端口
            Integer r1 = adminService.stopServer(msg);
            if (r1 != null && r1.equals(JsonResultStateCode.SUCCESS)) {
                Integer r2 = JsonResultStateCode.SUCCESS;
                if (msg.getServerType() == 0) {
                    if (SmtpServer.isShutDown()) {
                        r2 = adminService.restartServer(msg);
                    }
                } else if (msg.getServerType() == 1) {
                    if (Pop3Server.isShutDown()) {
                        r2 = adminService.restartServer(msg);
                    }
                }
                if (r2 != null && r2.equals(JsonResultStateCode.SUCCESS)) {
                    return res;
                } else {
                    return res1;
                }
            } else {
                return res1;
            }
        } else {
            return res1;
        }
    }

    @PostMapping("/change-server-name")
    @IsAdmin
    public JsonResult handleChangeServerName(@RequestBody ServerPortMsg msg) {

        //修改端口号
        Integer row = adminService.changeServerPort(msg);
        JsonResult res = JsonResultFactory.buildSuccessResult();
        JsonResult res1 = JsonResultFactory.buildFailureResult();
        if (row != null && row == 1) {
            //暂停当前端口
            Integer r1 = adminService.stopServer(msg);
            if (r1 != null && r1.equals(JsonResultStateCode.SUCCESS)) {
                Integer r2 = JsonResultStateCode.SUCCESS;
                if (msg.getServerType() == 0) {
                    if (!SmtpServer.isShutDown()) {
                        r2 = adminService.restartServer(msg);
                    }
                } else if (msg.getServerType() == 1) {
                    if (!Pop3Server.isShutDown()) {
                        r2 = adminService.restartServer(msg);
                    }
                }
                if (r2 != null && r2.equals(JsonResultStateCode.SUCCESS)) {
                    return res;
                } else {
                    return res1;
                }
            } else {
                return res1;
            }
        } else {
            return res1;
        }
    }

    @GetMapping("/get-server-msg")
    @IsAdmin
    public JsonResult handleGetServerMsg() {
        List<ServerMessage> serverMsg = adminService.getServersMsg();
        if (serverMsg != null) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.SUCCESS,
                    JsonResultStateCode.SUCCESS_DESC,
                    serverMsg
            );
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @GetMapping("/get-filters")
    public JsonResult handleGetFilters() {
        List<Filter> res = adminService.getFilters();
        if (res != null && res.size() > 0) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.SUCCESS,
                    JsonResultStateCode.SUCCESS_DESC,
                    res
            );
        } else if (res != null) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.NOT_FOUND,
                    JsonResultStateCode.NOT_FOUND_DESC,
                    null
            );
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/add-blacklist")
    @IsAdmin
    public JsonResult handleAddBlacklist(@RequestBody List<String> ipList) {

        Integer row = adminService.addFilter(ipList);
        if (row != null && row == ipList.size()) {
            return JsonResultFactory.buildSuccessResult();
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("del-blacklist")
    public JsonResult handleDelBlacklist(@RequestBody List<String> ipList) {

        Integer row = adminService.deleteFilter(ipList);
        if (row != null && row == ipList.size()) {
            return JsonResultFactory.buildSuccessResult();
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("update-mailbox-size")
    @IsAdmin
    public JsonResult handleUpdateMailboxSize(@RequestBody MailBoxSize mailBoxSize) {

        Integer row = adminService.updateMailBoxSize(mailBoxSize.getUsername(), mailBoxSize.getSize());
        if (row != null && row == mailBoxSize.getUsername().size()) {
            return JsonResultFactory.buildSuccessResult();
        } else if (row != null) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED,
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC,
                    null
            );
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @GetMapping("/get-logs")
    @IsAdmin
    public JsonResult getLogs() {

        List<Log> logs = adminService.getLogs();
        serverService.addServerNameForLogs(logs);
        if (logs != null) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.SUCCESS,
                    JsonResultStateCode.SUCCESS_DESC,
                    logs
            );
        } else {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.FAILED,
                    JsonResultStateCode.FAILED_DESC,
                    null
            );
        }
    }

    @PostMapping("/del-logs")
    @IsAdmin
    public JsonResult handleDelLog(@RequestBody List<Integer> logIdList) {

        Integer row = adminService.deleteLog(logIdList);
        if (row != null && row == logIdList.size()) {
            return JsonResultFactory.buildSuccessResult();
        } else if (row != null) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED,
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC,
                    null
            );
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/send-group-mail")
    @IsAdmin
    public JsonResult handleSendGroupMail(@RequestBody MassEmail massEmail) {
        JsonResult res;
        if (!serverService.isRightServer(massEmail.getSenderEmail())) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC, null);
            return res;
        }
        Integer row = adminService.sendMails(massEmail);
        if (row != null && row == massEmail.getReceiverEmails().size()) {
            return JsonResultFactory.buildSuccessResult();
        } else if (row != null) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED,
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC,
                    null
            );
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("forbid-users")
    @IsAdmin
    public JsonResult handleForbideUsers(@RequestBody ForbiddenUser forbiddenUser) {
        System.out.println(forbiddenUser);
        Integer row = adminService.filterUsers(forbiddenUser.getUsername(), forbiddenUser.getForbidden());

        if (row != null && row == forbiddenUser.getUsername().size()) {
            return JsonResultFactory.buildSuccessResult();
        } else if (row != null) {
            return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED,
                    JsonResultStateCode.OPERATION_IS_NOT_COMPLETED_DESC,
                    null
            );
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @GetMapping("/get-mails")
    @IsAdmin
    public JsonResult handleGetMails() {
        List<Email> emailList = adminService.getAllMails();
        System.out.println(emailList);
        if (emailList != null) {
            if (emailList.size() > 0) {
                return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.SUCCESS,
                    JsonResultStateCode.SUCCESS_DESC,
                    emailList
                );
            } else {
                return JsonResultFactory.buildJsonResult(
                    JsonResultStateCode.NOT_FOUND,
                    JsonResultStateCode.NOT_FOUND_DESC,
                    null
                );
            }
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }
}
