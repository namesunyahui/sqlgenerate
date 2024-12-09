package org.example.sqlgenerate.mongodb.service.Impl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.sqlgenerate.mongodb.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final GridFSBucket gridFSBucket;

    @Autowired
    public FileStorageServiceImpl(MongoDatabase database) {
        this.gridFSBucket = GridFSBuckets.create(database);
    }



    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 设置文件的元数据
        Document metadata = new Document();
        metadata.append("contentType", file.getContentType());
        // 创建上传选项并添加元数据
        GridFSUploadOptions options = new GridFSUploadOptions().metadata(metadata);
        // 使用GridFSBucket上传文件
        ObjectId uploadedFile = gridFSBucket.uploadFromStream(Objects.requireNonNull(file.getOriginalFilename()), file.getInputStream(), options);
        // 返回文件的ID，以便后续下载
        return uploadedFile.toHexString();
    }



    @Override
    public InputStream downloadFile(String fileId) throws IOException {
        // 使用GridFSBucket根据文件ID下载文件
        ObjectId objectId = new ObjectId(fileId);
        GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", objectId)).first();
        if (gridFSFile != null) {

            //gridFSBucket.downloadToStream(gridFSFile.getObjectId(), outputStream);
            return gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        } else {
            throw new FileNotFoundException("File not found with id: " + fileId);
        }
    }

    @Override
    public void deleteFile(String fileId)  {
        ObjectId objectId = new ObjectId(fileId);
        gridFSBucket.delete(objectId);
    }


}

