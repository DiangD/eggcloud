package com.qzh.eggcloud.controller;

import com.google.common.collect.Maps;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.SystemConst;
import com.qzh.eggcloud.common.annotation.AccessLimit;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.dfs.EggCom;
import com.qzh.eggcloud.dfs.model.EggFileInfo;
import com.qzh.eggcloud.dfs.utils.EggFileUtil;
import com.qzh.eggcloud.model.FileStore;
import com.qzh.eggcloud.model.StoreDetail;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.UserQuery;
import com.qzh.eggcloud.service.FileStoreService;
import com.qzh.eggcloud.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UserspaceController
 * @Author DiangD
 * @Date 2021/3/9
 * @Version 1.0
 * @Description 用户主页控制器
 **/
@RestController
@RequestMapping("/u")
@Validated
@Slf4j
public class UserspaceController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private FileStoreService fileStoreService;

    @PostMapping("/avatar")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> setUserAvatar(@RequestParam("userId") @NotNull(message = "user id不能为空") Long userId,
                                                            @RequestParam("avatar") @NotBlank(message = "头像不能为空")
                                                            @URL(message = "头像链接地址错误") String avatarUrl) {
        sysUserService.setUserAvatar(userId, avatarUrl);
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, null));
    }

    @GetMapping("/info")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<SysUserEntity>> getUserDetails(@RequestParam Long userId) {
        UserQuery query = UserQuery.builder()
                .userId(userId)
                .build();
        List<SysUserEntity> entities = sysUserService.findUserDetails(query);
        if (entities.size() == 1) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, entities.get(0)));
        }
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail, null));
    }

    @PostMapping("/update")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#user.getUserId())}")
    public ResponseEntity<JsonResult<Object>> updateUserInfo(@Validated  SysUserEntity user) throws BaseException {
        user.setPassword(null);
        user.setEmail(null);
        int row = sysUserService.updateUserInfo(user);
        if (row > 0) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, null));
        }
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail, null));
    }

    @PostMapping("/pwd")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> updateUserPassword(@RequestParam Long userId, String originalPassword,
                                                                 String newPassword) {
        SysUserEntity user = sysUserService.findByUserId(userId);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (user != null) {
            if (passwordEncoder.matches(originalPassword, user.getPassword())) {
                newPassword = passwordEncoder.encode(newPassword);
                SysUserEntity userEntity = SysUserEntity.builder()
                        .userId(userId)
                        .password(newPassword)
                        .build();
                int row = sysUserService.saveOrUpdateUser(userEntity);
                if (row > 0) {
                    return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, null));
                }
            } else {
                return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), "原密码不匹配", null));
            }
        }
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail, null));
    }

    @PostMapping("/upload/avatar")
    @AccessLimit(limit = 2, timeScope = 10)
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')}")
    public ResponseEntity<JsonResult<Object>> uploadAvatar(@RequestParam Long userId,
                                                           @RequestParam("file") MultipartFile file,
                                                           @RequestHeader HttpHeaders headers) throws IOException, BaseException {
        Map<String, String> header = null;
        String headerMD5 = headers.getFirst(SystemConst.HEADER_EGGCLOUD_FILE_HASH);
        if (StringUtils.isNotEmpty(headerMD5)) {
            header = Maps.newHashMap();
            header.put(EggCom.HEADER_FILE_HASH, headerMD5);
        }
        JsonResult<EggFileInfo> jsonResult = EggFileUtil.uploadFile(file, header);
        if (jsonResult.getStatus() == EggCom.UPLOAD_SUCCESS) {
            SysUserEntity user = sysUserService.findByUserId(userId);
            user.setAvatar(jsonResult.getData().getUrl());
            sysUserService.updateUserInfo(user);
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, null));
        }
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail, null));
    }

    @GetMapping("/store")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> getUserFileStoreInfo(@RequestParam Long userId) throws BaseException {
        FileStore store = fileStoreService.getStoreInfo(userId);
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, store));
    }

    @GetMapping("/store/detail")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> getUserFileStoreDetail(@RequestParam Long userId) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        StoreDetail storeDetail = fileStoreService.getStoreDetail(userDetail.getStoreId());
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, storeDetail));
    }

    @GetMapping("/menus")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<Object> getUserMenus(@RequestParam Long userId) {
        List<MenuDTO> menuTree = sysUserService.getUserMenuTree(userId);
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, menuTree));
    }


}
