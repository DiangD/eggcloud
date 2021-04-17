package com.qzh.eggcloud.common.interceptor;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName AvatarUploadInterceptor
 * @Author DiangD
 * @Date 2021/3/11
 * @Version 1.0
 * @Description 头像上传拦截器
 **/
@Component
public class AvatarUploadInterceptor implements HandlerInterceptor {
    //1MB
    private static final long MaxSize = 1048576;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile avatar = multipartRequest.getFile("file");
            if (avatar == null) {
                return true;
            }
            if (avatar.getSize() > MaxSize) {
                throw new MaxUploadSizeExceededException(MaxSize);
            }
            if (!checkExtJPGPNG(avatar.getOriginalFilename())) {
                throw new BaseException(ErrorCode.WrongFileFormat);
            }
        }
        return true;
    }

    /**
     * @param filename 文件名
     * @return 后缀是否正确
     * 只允许jpg、png格式
     */
    private boolean checkExtJPGPNG(String filename) {
        String extName = FileUtil.extName(filename);
        return StringUtils.compareIgnoreCase(extName, "jpg") == 0 ||
                StringUtils.compareIgnoreCase(extName, "png") == 0 ||
                StringUtils.compareIgnoreCase(extName, "jpeg") == 0;
    }
}
