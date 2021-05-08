package com.qzh.eggcloud;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.qzh.eggcloud.common.config.CloudConfig;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.common.utils.SpringUtil;
import com.qzh.eggcloud.dfs.utils.EggFileUtil;
import com.qzh.eggcloud.mapper.FileStoreMapper;
import com.qzh.eggcloud.mapper.SysMenuMapper;
import com.qzh.eggcloud.mapper.SysUserMapper;
import com.qzh.eggcloud.model.FileStore;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.UserQuery;
import com.qzh.eggcloud.service.SysFileService;
import com.qzh.eggcloud.service.SysUserService;
import com.qzh.eggcloud.service.impl.SysFileServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.*;

@SpringBootTest
class EggCloudApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test_upload_file() {
        HashMap<String, Object> paramMap = Maps.newHashMap();
        //文件上传只需将参数中的键指定（默认file），值设为文件对象即可，对于使用者来说，文件上传与普通表单提交并无区别
        paramMap.put("file", FileUtil.file("E:\\网站图片\\53939202_p0.jpg"));
        String result = HttpUtil.post("http://localhost:8081/upload", paramMap);
        System.out.println(result);
    }

    @Test
    void test_egg_dfs_config() {
        System.out.println(CloudConfig.text);
        System.out.println(CloudConfig.document);
        System.out.println(CloudConfig.image);
        System.out.println(CloudConfig.audio);
        System.out.println(CloudConfig.video);
        System.out.println(CloudConfig.zip);
    }

    @Test
    void test_file_ext() {
        String extName = FileUtil.extName("test.PNG");
        System.out.println(extName);
        System.out.println(StringUtils.compareIgnoreCase(extName, "jpg") == 0
                || StringUtils.compareIgnoreCase(extName, "png") == 0);
    }

    @Resource
    private FileStoreMapper fileStoreMapper;

    @Test
    void test_store_service() {
        FileStore store = fileStoreMapper.findFileStoreById(1L);
        System.out.println(store);
    }

    @Test
    void test_uuid() {

        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    @Resource
    private SysUserMapper userMapper;

    @Test
    void test_findUser() {
        List<SysUserEntity> user = userMapper.findUserDetailByUser(UserQuery.builder().build());
        System.out.println(user);
    }

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Test
    void test_menuTree() {
        List<MenuDTO> entities = sysMenuMapper.findMenuTreeByUserId(5L);
        System.out.println(JSON.toJSONString(entities));
    }


    @Test
    void test_hasPermission() {
        SysUserService sysUserService = SpringUtil.getBean(SysUserService.class);
        Set<String> permissions = sysUserService.getUserPermissions(5L);
        System.out.println(permissions);
    }

    @Autowired
    private SysFileServiceImpl sysFileService;

    @Test
    void test_getUserDirectory() {
        SysFileServiceImpl bean = SpringUtil.getBean(SysFileServiceImpl.class);
        String directory = bean.getUserDirectory("/qzh/test/123.txt");
        System.out.println("directory = " + directory);


        SysFile build = SysFile.builder()
                .name("test.md").isFolder(false)
                .path("/qzh/test/").build();
        String pathByFileId = bean.getRelativePathByFileId(build);
        System.out.println("pathByFileId = " + pathByFileId);

    }

    @Test
    void test_downloadBytes() {
        byte[] bytes = EggFileUtil.downloadBytes("g1", "2021/4/15/1382699849335308288.jpg");
        byte[] bytes1 = HttpUtil.downloadBytes("http://127.0.0.1:8082/g1/2021/4/15/1382699849335308288.jpg");
        String s1 = Arrays.toString(bytes);
        String s2 = Arrays.toString(bytes1);
        System.out.println(s1.equals(s2));
    }

    @Test
    void test_caseFormat() {
        String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "test_data");
        System.out.println("to = " + to);
    }

    @Test
    void test_hasPermissions() {
        System.out.println("SecurityUtil.PERMISSIONS = " + SecurityUtil.PERMISSIONS);
    }
}
