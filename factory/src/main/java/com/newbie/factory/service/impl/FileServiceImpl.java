package com.newbie.factory.service.impl;

import com.google.common.collect.Lists;
import com.newbie.factory.service.IFileService;
import com.newbie.factory.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path) {
        //获得文件名称
        String fileName = file.getOriginalFilename();
        String suffixFile = fileName.substring(fileName.lastIndexOf("."));
        String createrFileName = UUID.randomUUID().toString() + suffixFile;
        logger.info("开始上传文件，上传文件名：{}，文件后缀 ：{} ，新文件名：{}" ,fileName,suffixFile,createrFileName);

        //根据文件路径查看 是否有文件夹
        File fileDir = new File(path);
        if (!fileDir.exists()){//测试此抽象路径名表示的文件或目录是否存在 存在返回 true
            fileDir.setWritable(true);//设置此抽象路径名的所有者的写权限
            fileDir.mkdirs();//生成所有目录
        }


        File targetFile = new File(path, createrFileName);

        try {
            file.transferTo(targetFile);
            logger.info("文件已经上传成功！~");

            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            logger.info("文件已经上传服务器");

            targetFile.delete();//删除文件
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
