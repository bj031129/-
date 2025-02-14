package com.example.server.controller;

import com.example.server.config.SpringContextConfig;
import com.example.server.dto.MassEmail;
import com.example.server.dto.UserPhoneMsg;
import com.example.server.entity.Contact;
import com.example.server.entity.ContactMsg;
import com.example.server.entity.Email;
import com.example.server.entity.ServerMessage;
import com.example.server.service.AdminService;
import com.example.server.service.ServerService;
import com.example.server.service.UserService;
import com.example.server.service.impl.AdminServiceImpl;
import com.example.server.service.impl.UserServiceImpl;
import com.example.server.util.annotation.IsAdmin;
import com.example.server.util.annotation.IsLogin;
import com.example.server.util.http.CookieUtils;
import com.example.server.util.json.JsonResult;
import com.example.server.util.json.JsonResultFactory;
import com.example.server.util.json.JsonResultStateCode;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 曾佳宝
 */
@Api
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService = SpringContextConfig.getBean(UserServiceImpl.class);
    private final ServerService serverService = SpringContextConfig.getBean("ServerServiceImpl");
    private final AdminService adminService = SpringContextConfig.getBean(AdminServiceImpl.class);

    @GetMapping("/get-server-msg")
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

    @GetMapping("/get-contact")
    public JsonResult handleGetContact(HttpServletRequest request) {
        String username = CookieUtils.findCookie(request.getCookies(), "username").getValue();
        List<ContactMsg> contactList = userService.getContactList(username);
        if (contactList != null) {
            if (contactList.size() > 0) {
                return JsonResultFactory.buildJsonResult(
                        JsonResultStateCode.SUCCESS,
                        JsonResultStateCode.SUCCESS_DESC,
                        contactList
                );
            } else {
                return JsonResultFactory.buildJsonResult(
                        JsonResultStateCode.NOT_FOUND,
                        JsonResultStateCode.NOT_FOUND_DESC,
                        null
                );
            }
        }
        return JsonResultFactory.buildFailureResult();
    }

    @PostMapping("/add-contact")
    public JsonResult handleAddContact(@RequestBody Contact contact) {

        Integer row = userService.addContact(contact);
        if (row != null && row == 1) {
            return JsonResultFactory.buildSuccessResult();
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/del-contact")
    public JsonResult handleDelContact(@RequestBody Contact contact) {
        Integer row = userService.deleteContact(contact);
        if (row != null && row == 1) {
            return JsonResultFactory.buildSuccessResult();
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/update-phone")
    public JsonResult handleUpdatePhone(@RequestBody UserPhoneMsg msg) {

        Integer row = userService.updatePhone(msg);
        if (row != null && row == 1) {
            return JsonResultFactory.buildSuccessResult();
        } else {
            return JsonResultFactory.buildFailureResult();
        }
    }

    @PostMapping("/get-receive-mails")
    public JsonResult handleGetReceiveMails(@RequestParam(name = "username")String username ) {
        if (!serverService.isRightServer(username)) {
            return JsonResultFactory.buildJsonResult(
                JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC,
                null);
        }
        username = username.split("@")[0];
        List<Email> emailList = userService.getMailsByReceiver(username);
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

    @PostMapping("/get-send-mails")
    public JsonResult handleGetSendMails(@RequestParam(name = "username")String username ) {
        if (!serverService.isRightServer(username)) {
            return JsonResultFactory.buildJsonResult(
                JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC,
                null);
        }
        username = username.split("@")[0];
        List<Email> emailList = userService.getMailsBySender(username);
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

    @PostMapping("/add-mail")
    public JsonResult handleSendGroupMail(@RequestParam(name = "senderAddress")String senderAddress,
        @RequestParam(name = "reciverAddress")String reciverAddress,
        @RequestParam(name = "subject")String subject,
        @RequestParam(name = "content")String content) {
        System.out.println(senderAddress+reciverAddress+subject+content);
        JsonResult res;
        if (!serverService.isRightServer(senderAddress) || !serverService.isRightServer(reciverAddress)) {
            res = JsonResultFactory.buildJsonResult(JsonResultStateCode.UNAUTHORIZED,
                JsonResultStateCode.UNAUTHORIZED_DESC, null);
            return res;
        }
        MassEmail massEmail = new MassEmail(senderAddress, reciverAddress, subject, content);
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
}
