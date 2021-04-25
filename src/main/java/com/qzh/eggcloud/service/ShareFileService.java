package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.ShareFile;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.auth.dto.ShareDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.query.ShareQuery;

public interface ShareFileService {
    ShareFile generateShareFile(ShareFile shareFile) throws BaseException;

    ShareDTO getShareFile(String accessKey);

    void cancelShares(String[] accessKey, Long storeId);

    PageInfo<ShareDTO> getUserShareList(ShareQuery query, PageEntity pageEntity);

    void removeShareFilesExpired();

    PageInfo<SysFile> accessShareDirOpen(String accessKey, Long folderId, PageEntity entity) throws BaseException;
}
