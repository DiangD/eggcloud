package com.qzh.eggcloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName RespUtil
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 返回值工具类
 **/
@Slf4j
public class RespUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        mapper.registerModule(javaTimeModule);
    }


    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<>(ErrorCode.Success.getCode(),
                ErrorCode.Success.getMsg(), data);
    }

    public static <T> JsonResult<T> uploadSuccess(T data) {
        return new JsonResult<>(ErrorCode.Success.getCode(),
                "文件上传成功", data);
    }

    public static <T> JsonResult<T> fail(T data) {
        return new JsonResult<>(ErrorCode.Fail.getCode(), ErrorCode.Fail.getMsg(), data);
    }

    public static <T> JsonResult<T> uploadFail(T data) {
        return new JsonResult<>(ErrorCode.Fail.getCode(), "文件上传失败", data);
    }

    public static <T> JsonResult<T> generate(int code, String msg, T data) {
        return new JsonResult<>(code, msg, data);
    }

    public static <T> JsonResult<T> generate(ErrorCode errorCode, T data) {
        return new JsonResult<>(errorCode.getCode(), errorCode.getMsg(), data);
    }

    public static void responseJson(ServletResponse response, JsonResult res) {
        PrintWriter out = null;
        try {
            response.setContentType("application/json; charset=UTF-8");
            out = response.getWriter();
            out.println(mapper.writeValueAsString(mapper.writeValueAsString(res)));
        } catch (Exception e) {
            log.error("【JSON输出异常】" + e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
