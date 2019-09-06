package com.newbie.factory.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Andy-J<br>
 * @version 1.0<br>
 * @createDate 2019/9/4 12:57 <br>
 * @desc FTP工具类
 */
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    @Value("ftp.server.ip")
    private static String ftpIp;
    @Value("ftp.user")
    private static String ftpUser;
    @Value("ftp.pass")
    private static String ftpPass;

    public FTPUtil(String ip , int port , String user , String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
        logger.info("开始链接FTP 服务器");
        boolean flag = ftpUtil.uploadFile("img", fileList);
        logger.info("上传结束！~");
        return flag;
    }

    private boolean uploadFile(String remotePath , List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }

            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean flag = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            flag = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("链接ftp服务器异常",e);
        }
        return flag;
    }


    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
