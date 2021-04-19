package com.qzh.eggcloud.service.impl;

import cn.hutool.core.lang.UUID;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.VilifyCodeUtil;
import com.qzh.eggcloud.model.ShareFile;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.auth.dto.ShareDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.query.ShareQuery;
import com.qzh.eggcloud.service.ShareFileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName ShareFileServiceImpl
 * @Author DiangD
 * @Date 2021/3/16
 * @Version 1.0
 * @Description
 **/
@Service
public class ShareFileServiceImpl extends BaseService implements ShareFileService {
    /**
     * @param shareFile 分享实体
     * @return 完善的实体
     * @throws BaseException 统一异常
     */
    @Override
    public ShareFile generateShareFile(ShareFile shareFile) throws BaseException {
        ShareFile shareDo = shareFileMapper.findShareFileByFileId(shareFile.getFileId(), shareFile.getStoreId());
        if (shareDo != null) {
            throw new BaseException(ErrorCode.Fail.getCode(), "该文件(夹)已在分享列表");
        }
        shareFile = buildShareFile(shareFile);
        SysFile file = fileMapper.findById(shareFile.getFileId(), shareFile.getStoreId());
        if (file == null) {
            throw new BaseException(ErrorCode.InvalidParam);
        }
        shareFileMapper.insertShareFile(shareFile);
        return shareFile;
    }

    /**
     * @param accessKey 唯一标识
     * @return 实体
     */
    @Override
    public ShareDTO getShareFile(String accessKey) {
        ShareDTO file = shareFileMapper.findShareFileByAccessKey(accessKey);
        if (!isValidShareFile(file)) {
            shareFileMapper.deleteShareFileByAccessKey(file.getAccessKey(), file.getStoreId());
            return null;
        }
        SysFile sysFile = fileMapper.findById(file.getFileId(), file.getStoreId());
        file.setFilename(sysFile.getName());
        file.setContentType(sysFile.getContentType());
        file.setExtension(sysFile.getExtension());
        return file;
    }

    /**
     * @param accessKeys 唯一标识
     * @param storeId   仓库id
     */
    @Override
    public void cancelShares(String[] accessKeys, Long storeId) {
        List<String> asList = Arrays.asList(accessKeys);
        shareFileMapper.deleteShareFileByAccessKeys(asList, storeId);
    }

    /**
     * @param query    仓库id
     * @param pageEntity page
     * @return 分页
     */
    @Override
    public PageInfo<ShareDTO> getUserShareList(ShareQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<ShareDTO> files = shareFileMapper.findShareByQuery(query);
        return PageInfo.of(files);
    }

    /**
     * 删除过期的分享
     */
    @Override
    public void removeShareFilesExpired() {
        shareFileMapper.deleteByExpireAt();
    }

    /**
     * @param file 分享实体
     * @return 是否有效
     */
    public Boolean isValidShareFile(ShareDTO file) {
        if (file != null) {
            LocalDateTime expireAt = file.getExpireAt();
            if (expireAt == null) {
                return true;
            }
            return expireAt.compareTo(LocalDateTime.now()) > 0;
        }
        return false;
    }


    /**
     * @param shareFile 分享实体
     * @return 完善的实体
     */
    public ShareFile buildShareFile(ShareFile shareFile) {
        String accessKey = genAccessKey();
        shareFile.setAccessKey(accessKey);
        if (shareFile.getIsFolder() == null) {
            shareFile.setIsFolder(false);
        }
        if (shareFile.getHasVerify()) {
            shareFile.setCode(VilifyCodeUtil.createVilifyCode(6));
        }
        return shareFile;
    }


    /**
     * @return 唯一标识
     */
    private String genAccessKey() {
        return UUID.fastUUID().toString().replaceAll("-", "");
    }
}

