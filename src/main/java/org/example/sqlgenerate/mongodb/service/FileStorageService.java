package org.example.sqlgenerate.mongodb.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *  GridFS 大文件 文件存储服务
 */
public interface FileStorageService {

    /**
     * 文件上传
     * @param file 文件
     * @return 文件id
     * @throws IOException 异常
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 文件下载
     *
     * @param fileId       文件id
     * @return
     * @throws IOException 异常
     */
    InputStream downloadFile(String fileId) throws IOException;

    /**
     * 删除文件
     * @param fileId 文件id
     * @throws IOException
     */
    void deleteFile(String fileId) ;
}
