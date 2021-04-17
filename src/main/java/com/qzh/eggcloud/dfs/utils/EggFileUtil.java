package com.qzh.eggcloud.dfs.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.qzh.eggcloud.common.config.CloudConfig;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.dfs.EggDfsConfig;
import com.qzh.eggcloud.dfs.model.EggFileInfo;
import com.qzh.eggcloud.model.FileType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Map;

/**
 * @ClassName FileUtil
 * @Author DiangD
 * @Date 2021/3/9
 * @Version 1.0
 * @Description EggFileUtil
 **/
public class EggFileUtil {

    private static final String uploadUrl = EggDfsConfig.getUploadUrl();
    private static final String deleteUrl = EggDfsConfig.getDeleteUrl();
    private static final String downloadUrl = EggDfsConfig.getDownloadUrl();

    public static InputStreamResource buildResource(MultipartFile file) throws IOException {
        InputStreamResource isr;
        isr = new InputStreamResource(file.getInputStream(), file.getOriginalFilename());
        return isr;
    }

    public static InputStreamResource buildResource(InputStream in, String filename) {
        return new InputStreamResource(in, filename);
    }

    public static JsonResult<EggFileInfo> getResponse(String result) {
        return JSON.parseObject(result, new TypeReference<JsonResult<EggFileInfo>>() {
        });
    }

    public static JsonResult<EggFileInfo> uploadFile(MultipartFile file, Map<String, String> header) throws IOException {
        InputStreamResource isr = buildResource(file);
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("file", isr);
        String result = HttpRequest.post(uploadUrl).addHeaders(header).form(paramMap).execute().body();
        System.out.println(result);
        return getResponse(result);
    }

    public static JsonResult<EggFileInfo> uploadFile(InputStream in, String filename, Map<String, String> headers) throws IOException {
        InputStreamResource isr = buildResource(in, filename);
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("file", isr);
        String result = HttpRequest.post(uploadUrl).addHeaders(headers).form(paramMap).execute().body();
        return getResponse(result);
    }

    /**
     * @param fileName 文件名
     * @return 文件類型
     * 0:文本类型  1:文档类型   2:图像类型  3:视频类型  4:音乐类型  5:其他类型
     */
    public static FileType getFileType(String fileName) {
        String ext = FileUtil.extName(fileName);
        if (CloudConfig.text.contains(ext)) {
            return FileType.Text;
        } else if (CloudConfig.document.contains(ext)) {
            return FileType.Doc;
        } else if (CloudConfig.video.contains(ext)) {
            return FileType.Video;
        } else if (CloudConfig.audio.contains(ext)) {
            return FileType.Audio;
        } else if (CloudConfig.zip.contains(ext)) {
            return FileType.Zip;
        } else if (CloudConfig.image.contains(ext)) {
            return FileType.Image;
        } else {
            return FileType.Other;
        }
    }

    public static String generateFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        String ext = FileUtil.extName(fileName);
        long timeMillis = Instant.now().toEpochMilli();
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        return prefix + "-" + timeMillis + "." + ext;
    }

    public static void downloadFile(String group, String filepath, OutputStream outputStream) {
        String url = downloadUrl + "?" +
                "group=" + group +
                "&" +
                "file=" + filepath;
        HttpUtil.download(url, outputStream, true);
    }

    public static void downloadFile(Map<String, String> params, OutputStream outputStream) {
        StringBuilder builder = new StringBuilder(downloadUrl);
        if (params.isEmpty()) {
            return;
        }
        builder.append("?");
        for (String key : params.keySet()) {
            builder.append(key)
                    .append("=")
                    .append(params.get(key))
                    .append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        HttpUtil.download(builder.toString(), outputStream, true);
    }

}
