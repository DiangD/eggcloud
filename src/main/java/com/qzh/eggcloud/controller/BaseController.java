package com.qzh.eggcloud.controller;

import cn.hutool.extra.mail.MailUtil;
import com.qzh.eggcloud.common.annotation.AccessLimit;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.VilifyCodeUtil;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BaseController
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 基础控制器
 **/
@RestController
@Validated
public class BaseController {

    @Value("${validation.code.length}")
    private Integer length;

    @Autowired
    private SysUserService userService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/verify/code")
    @AccessLimit(limit = 1, timeScope = 10, timeUnit = TimeUnit.SECONDS)
    public ResponseEntity<JsonResult<Object>> vilifyCode(@Email(message = "邮箱格式错误")
                                                         @NotBlank(message = "邮箱不能为空")
                                                         @RequestParam("email") String email) {
        SysUserEntity user = userService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), "邮箱已经注册", null));
        }
        String code = VilifyCodeUtil.createVilifyCode(length);
        MailUtil.send(email, "EGGCLOUD验证码",
                VilifyCodeUtil.content(code, VilifyCodeUtil.DEFAULT_CODE_EXPIRE_MIN), false);
        //生成key
        String key = VilifyCodeUtil.KEY_CODE_INDEX + email;
        //配置文件是以json存储，不方便直接操作字符串，修改为字符串系列化
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(key, code, VilifyCodeUtil.DEFAULT_CODE_EXPIRE_MIN, TimeUnit.MINUTES);
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success.getCode(),
                "验证码已成功发到你的邮箱，请在30分钟内完成注册~", null));
    }


    @PostMapping("/register")
    public ResponseEntity<JsonResult<Object>> register(@Validated SysUserEntity user,
                                                       @NotBlank(message = "验证码不能为空") String code) {
        //生成key
        String key = VilifyCodeUtil.KEY_CODE_INDEX.concat(user.getEmail());
        //修改为字符串序列化
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        String validate = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(validate) && code.equals(validate)) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodePwd = passwordEncoder.encode(user.getPassword());
            user.setCreateAt(LocalDateTime.now());
            user.setPassword(encodePwd);
            if (userService.registerUser(user)) {
                redisTemplate.delete(key);
                return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, null));
            }
        } else if (StringUtils.isNotEmpty(validate) && !code.equals(validate)) {
            return ResponseEntity.ok(new JsonResult<>(ErrorCode.Fail.getCode(), "验证失败！", null));
        } else {
            //不管邮箱有没有拉取过验证码，一律为验证码失效
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), "验证码失效！", null));
        }
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), "注册失败", null));
    }

    @PostMapping("/exist/username")
    public ResponseEntity<Object> isExistUsername(@RequestParam String username) {
        SysUserEntity userEntity = userService.findByUsername(username);
        return ResponseEntity.ok(RespUtil.success(userEntity != null));
    }
}
