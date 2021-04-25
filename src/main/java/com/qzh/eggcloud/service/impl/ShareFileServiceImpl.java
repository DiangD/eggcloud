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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SysFileServiceImpl sysFileService;

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
        SysFile file = fileMapper.findById(shareFile.getFileId(),shareFile.getStoreId());
        if (file == null) {
            throw new BaseException(ErrorCode.InvalidParam);
        }
        shareFile = buildShareFile(shareFile, file);
        shareFileMapper.insertShareFile(shareFile);
        return shareFile;
    }

    /**
     * @param accessKey 唯一标识
     * @return 实体
     */
    @Override
    public ShareDTO getShareFile(String accessKey) {
        ShareDTO share = shareFileMapper.findShareFileByAccessKey(accessKey);
        if (share == null) {
            return null;
        }
        if (!isValidShareFile(share)) {
            shareFileMapper.deleteShareFileByAccessKey(share.getAccessKey(), share.getStoreId());
            return null;
        }
        return share;
    }

    /**
     * @param accessKeys 唯一标识
     * @param storeId    仓库id
     */
    @Override
    public void cancelShares(String[] accessKeys, Long storeId) {
        List<String> asList = Arrays.asList(accessKeys);
        shareFileMapper.deleteShareFileByAccessKeys(asList, storeId);
    }

    /**
     * @param query      仓库id
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

    @Override
    public PageInfo<SysFile> accessShareDirOpen(String accessKey, Long folderId, PageEntity entity) throws BaseException {
        ShareDTO share = shareFileMapper.findShareFileByAccessKey(accessKey);
        SysFile file = fileMapper.findShareFileById(share.getFileId());
        SysFile target = fileMapper.findById(folderId,share.getStoreId());
        if (!target.getIsFolder()) {
            throw new BaseException(ErrorCode.Fail);
        }
        if (!folderId.equals(file.getId()) && target.getPath().contains(sysFileService.getUserDirectory(file.getPath() + file.getName())))
            PageHelper.startPage(entity.getPageNum(), entity.getPageSize());
        String path = sysFileService.getUserDirectory(target.getPath() + target.getName());
        List<SysFile> files = fileMapper.findByPath(path, share.getStoreId());
        return PageInfo.of(files);
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
    public ShareFile buildShareFile(ShareFile shareFile, SysFile file) {
        String accessKey = genAccessKey();
        shareFile.setAccessKey(accessKey);
        shareFile.setIsFolder(file.getIsFolder());
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

