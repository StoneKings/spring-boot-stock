package com.chen.account.service.impl;

import com.chen.account.constant.AccountConstant;
import com.chen.account.dao.UserMapper;
import com.chen.account.dao.UserMapperExtends;
import com.chen.account.entity.SendSmsResponse;
import com.chen.account.entity.User;
import com.chen.account.service.IAccountService;
import com.chen.common.http.entity.Response;
import com.chen.common.http.res.TransmitUtils;
import com.chen.common.utils.AliyunMessageUtil;
import com.chen.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author long
 * <p>
 * date 17-9-4
 * <p>
 * desc
 */
@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private UserMapperExtends userMapper;

    @Override
    public Response getVerifyCode(String phoneNumber) {
        SendSmsResponse sendSmsResponse = AliyunMessageUtil.requestSmsCode(phoneNumber);
        SendSmsResponse.Result result;
        SendSmsResponse.Error_response errorResponse = null;
        try {
            result = sendSmsResponse.getAlibaba_aliqin_fc_sms_num_send_response().getResult();
            if (result.getSuccess()) {
                return TransmitUtils.transmitResponse(true, AccountConstant.SEND_SMS_CODE_SUCCESS, null);
            }
        } catch (NullPointerException e) {
            errorResponse = sendSmsResponse.getError_response();
        }
        return TransmitUtils.transmitErrorResponse(AccountConstant.SEND_SMS_CODE_FAIL, errorResponse.getCode(), errorResponse.getSub_msg());
    }

    @Override
    public Response register(String phoneNumber, String password, String randomStr) {
        //判断查询到的手机号是否为空,不为空就是账号已存在
        if (userMapper.selectByPhone(phoneNumber) != null) {
            return TransmitUtils.transmitErrorResponse(AccountConstant.SIGN_UP_USER_ALREADY_EXIST,
                    AccountConstant.CODE_SIGN_UP_ALREADY_EXIST, AccountConstant.SIGN_UP_USER_ALREADY_EXIST);
        }
        //这里开始加密密码
        password = StringUtils.md5(password, randomStr);
        User user = new User();
        long createAt = System.currentTimeMillis();
        user.setCreateat(createAt);
        user.setUpdateat(createAt);
        user.setPhone(phoneNumber);
        user.setPassword(password);
        user.setRandomstr(randomStr);
        int signUpRes = userMapper.insert(user);
        if (signUpRes == 1) {
            user.setPassword("");
            user.setRandomstr("");
            return TransmitUtils.transmitResponse(true, AccountConstant.SIGN_UP_SUCCESS, user);
        } else {
            return TransmitUtils.transmitErrorResponse(AccountConstant.SIGN_UP_FAIL, AccountConstant.CODE_SIGN_UP_FAIL, AccountConstant.SIGN_UP_FAIL);
        }
    }

    @Override
    public Response login(String phoneNumber, String password) {
        User user = userMapper.selectByPhone(phoneNumber);
        //检测用户是否存在
        if (user == null) {
            System.out.println("用户不存在");
            return TransmitUtils.transmitErrorResponse(AccountConstant.LOGIN_FAIL, AccountConstant.CODE_LOGIN_USER_NOT_EXIST,
                    AccountConstant.LOGIN_USER_NOT_EXIST);
        }

        String randomStr = user.getRandomstr();
        String psd = StringUtils.md5(password, randomStr);

        //登录成功
        if (psd.equals(user.getPassword())) {
            user.setPassword("");
            user.setRandomstr("");
            return TransmitUtils.transmitResponse(true,
                    AccountConstant.LOGIN_SUCCESS, user);
        } else {
            //用户名密码不匹配
            return TransmitUtils.transmitErrorResponse(
                    AccountConstant.LOGIN_FAIL,
                    AccountConstant.CODE_LOGIN_PASSWORD_ERROR,
                    AccountConstant.LOGIN_PASSWORD_ERROR
            );
        }
    }
}
