package com.example.server.util.json;

import com.example.server.entity.SmtpResult;

/**
 * 生成某个状态码对应的Json对象
 *
 * @author 曾佳宝
 */
public class JsonResultFactory {

    public static JsonResult buildJsonResult(Integer stateCode, String message, Object body) {
        return new JsonResult(stateCode, message, body);
    }

    public static JsonResult buildSuccessResult(SmtpResult smtpResult) {
        return buildJsonResult(JsonResultStateCode.SUCCESS, "", smtpResult);
    }

    public static JsonResult buildSuccessResult() {
        return buildJsonResult(JsonResultStateCode.SUCCESS, JsonResultStateCode.SUCCESS_DESC, null);
    }

    public static JsonResult buildFailureResult() {
        return JsonResultFactory.buildJsonResult(JsonResultStateCode.FAILED, JsonResultStateCode.FAILED_DESC, null);
    }

    public static JsonResult buildSmtpCommandErrorResult() {
        return buildSuccessResult(new SmtpResult(SmtpStateCode.COMMAND_ERROR, SmtpStateCode.COMMAND_ERROR_DESC));
    }
}
