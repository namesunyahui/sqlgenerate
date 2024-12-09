package org.example.sqlgenerate.mongodb.service;

import org.example.sqlgenerate.mongodb.entity.FileInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 继承MongoRepository的接口，以便对MongoDB执行CRUD
 */
public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
}
