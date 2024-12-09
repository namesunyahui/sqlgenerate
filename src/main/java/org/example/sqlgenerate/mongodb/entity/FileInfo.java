package org.example.sqlgenerate.mongodb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;

@Document(collection = "files")
@Data
public class FileInfo {
    @Id
    private String id;
    private String fileName;
    private String contentType;
    private long fileSize;
    private String uploadDate;
    private byte[] fileContent;

}
