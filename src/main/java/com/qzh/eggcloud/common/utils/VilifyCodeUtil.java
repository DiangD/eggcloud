package com.qzh.eggcloud.common.utils;

import java.util.Random;

/**
 * @ClassName VilifyCodeUtil
 * @Author DiangD
 * @Date 2021/3/7
 * @Version 1.0
 * @Description 验证码工具类
 **/
public class VilifyCodeUtil {
    /**
     * 默认验证码长度为6
     */
    private static final int DEFAULT_CODE_LENGTH = 6;
    public static final String KEY_CODE_INDEX = "EGGCLOUD:VERIFY:EMAIL:";
    public static final int DEFAULT_CODE_EXPIRE_MIN = 30;

    /**
     * @param length 长度
     * @return 验证码
     */
    public static String createVilifyCode(Integer length) {
        if (length == null || length <= 0) {
            length = DEFAULT_CODE_LENGTH;
        }
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * @param code 验证码
     * @param timeout 过期时长
     * @return 格式化后的邮件内容
     */
    public static String content(String code,Integer timeout) {
        String str = "您的验证码是%s。%n请马上前往验证，该验证码在%s分钟后过期！";
        return String.format(str, code, timeout);
    }

}
