package com.example.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.server.config.SpringContextConfig;
import com.example.server.dto.MassEmail;
import com.example.server.dto.UserMessage;
import com.example.server.entity.User;
import com.example.server.service.AuthService;
import com.example.server.service.ServerService;
import com.example.server.util.annotation.IsAdmin;
import com.example.server.util.json.JsonResult;
import com.example.server.util.json.JsonResultFactory;
import com.example.server.util.json.JsonResultStateCode;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 曾佳宝
 */
@Api
@RestController
@CrossOrigin
public class LoginController {

    private final AuthService authService = SpringContextConfig.getBean("LoginServiceImpl");
    private final ServerService serverService = SpringContextConfig.getBean("ServerServiceImpl");

    @PostMapping("/user/login")
    public JsonResult handleLogin(@RequestParam(name = "username") String username,
        @RequestParam(name = "password") String password) {

        JsonResult res;
        if (!serverService.isRightServer(username)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC, null);
            return res;
        }
        username = username.split("@")[0];
        String msg = authService.handleLogin(username, password);
        if (JsonResultStateCode.USERNAME_WRONG_DESC.equals(msg)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.USERNAME_WRONG, msg, null);
        } else if (JsonResultStateCode.PASSWORD_WRONG_DESC.equals(msg)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.PASSWORD_WRONG, msg, null);
        } else if (JsonResultStateCode.SUCCESS_DESC.equals(msg)) {
            res = JsonResultFactory.buildSuccessResult();
        } else if (JsonResultStateCode.USER_IS_LOG_OUT_DESC.equals(msg)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.USER_IS_LOG_OUT, msg, null);
        } else {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNKNOWN_ERROR, msg, null);
        }
        return res;
    }

    @PostMapping("/admin/login")
    public JsonResult handleLogin(@RequestBody UserMessage userMessage) {

        JsonResult res;
        String username = userMessage.getUsername();
        if (!serverService.isRightServer(username)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC, null);
            return res;
        }
        username = username.split("@")[0];
        String password = userMessage.getPassword();
        String msg = authService.handleLogin(username, password);
        System.out.printf("msg");
        if (JsonResultStateCode.USERNAME_WRONG_DESC.equals(msg)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.USERNAME_WRONG, msg, null);
        } else if (JsonResultStateCode.PASSWORD_WRONG_DESC.equals(msg)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.PASSWORD_WRONG, msg, null);
        } else if (JsonResultStateCode.SUCCESS_DESC.equals(msg)) {
            res = JsonResultFactory.buildSuccessResult();
        } else if (JsonResultStateCode.USER_IS_LOG_OUT_DESC.equals(msg)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.USER_IS_LOG_OUT, msg, null);
        } else {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNKNOWN_ERROR, msg, null);
        }
        return res;
    }

    @PostMapping("/register")
    public JsonResult handleRegister(@RequestParam(name = "username") String username,
        @RequestParam(name = "password") String password) {
        JsonResult res;
        if (!serverService.isRightServer(username)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC, null);
            return res;
        }
        username = username.split("@")[0];
        UserMessage userMessage = new UserMessage(username, password);
        Integer rows = authService.registerUser(userMessage);
        if (rows != null && rows == 1) {
            return JsonResultFactory.buildSuccessResult();
        } else if (rows != null && rows.equals(JsonResultStateCode.USERNAME_IS_EXITED)) {
            return JsonResultFactory.buildJsonResult(JsonResultStateCode.USERNAME_IS_EXITED,
                JsonResultStateCode.USERNAME_IS_EXITED_DESC, null);
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/update-password")
    public JsonResult handleUpdatePassword(@RequestBody JSONObject jsonObject) {
        String userName = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String newPassword = jsonObject.getString("newPassword");
        System.out.println(userName);
        System.out.println(password);
        System.out.println(newPassword);
        JsonResult res;
        if (!serverService.isRightServer(userName)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC, null);
            return res;
        }
        userName = userName.split("@")[0];

        User user = authService.findUserByUsername(userName);
        if (user == null || !user.getPassword().equals(password)) {
            return JsonResultFactory.buildFailureResult();
        }
        Integer rows = authService.updatePassword(userName, newPassword);
        if (rows != null && rows == 1) {
            return JsonResultFactory.buildSuccessResult();
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }
}
